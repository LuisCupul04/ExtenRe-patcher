/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.extenre.patcher

import com.extenre.patcher.extensions.InstructionExtensions.instructionsOrNull
import com.extenre.patcher.patch.BytecodePatchContext
import com.extenre.patcher.patch.PatchException
import com.extenre.patcher.util.PatchClasses
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.util.MethodUtil

/**
 * A fingerprint for a method. A fingerprint is a partial description of a method,
 * used to uniquely match a method by its characteristics.
 *
 * @param accessFlags The exact access flags using values of [AccessFlags].
 * @param returnType The return type. Compared using [String.startsWith].
 * @param parameters The parameters. Partial matches allowed and follow the same rules as [returnType].
 * @param filters A list of filters to match, declared in the same order the instructions appear in the method.
 * @param strings A list of strings that appear anywhere in the method in any order. Compared using [String.contains].
 * @param custom A custom condition for this fingerprint.
 */
class Fingerprint internal constructor(
    internal val accessFlags: Int?,
    internal val returnType: String?,
    internal val parameters: List<String>?,
    internal val filters: List<InstructionFilter>?,
    internal val strings: List<String>?,
    internal val custom: ((method: Method, classDef: ClassDef) -> Boolean)?,
) {
    private var _matchOrNull: Match? = null

    /**
     * Clears the current match, forcing this fingerprint to resolve again.
     */
    fun clearMatch() {
        _matchOrNull = null
    }

    /**
     * Returns the match for this fingerprint, or `null` if no match exists.
     */
    fun matchOrNull(patchContext: BytecodePatchContext): Match? {
        if (_matchOrNull != null) return _matchOrNull

        val fingerprintStrings = mutableListOf<String>()
        if (strings != null) {
            fingerprintStrings.addAll(strings)
        }

        if (filters != null) {
            fun findStringFilterLiterals(list: List<InstructionFilter>) =
                list.filterIsInstance<StringFilter>().map { it.stringValue }

            fingerprintStrings.addAll(findStringFilterLiterals(filters))

            filters.filterIsInstance<AnyInstruction>().forEach { anyFilter ->
                fingerprintStrings.addAll(findStringFilterLiterals(anyFilter.filters))
            }
        }

        fun matchAllClassMethods(value: PatchClasses.ClassDefWrapper): Match? {
            val classDef = value.classDef
            value.classDef.methods.forEach { method ->
                val match = matchOrNull(method, classDef, patchContext)
                if (match != null) {
                    _matchOrNull = match
                    return match
                }
            }
            return null
        }

        if (fingerprintStrings.isNotEmpty()) {
            fingerprintStrings.forEach { string ->
                patchContext.patchClasses.getClassFromOpcodeStringLiteral(string)?.forEach { stringClass ->
                    val match = matchAllClassMethods(stringClass)
                    if (match != null) return match
                }
            }

            patchContext.patchClasses.getAllClassesWithStrings().forEach { wrapper ->
                val match = matchAllClassMethods(wrapper)
                if (match != null) return match
            }
        }

        patchContext.patchClasses.classMap.values.forEach { wrapper ->
            val match = matchAllClassMethods(wrapper)
            if (match != null) return match
        }

        return null
    }

    /**
     * Match using a [ClassDef].
     */
    fun matchOrNull(
        classDef: ClassDef,
        patchContext: BytecodePatchContext,
    ): Match? {
        if (_matchOrNull != null) return _matchOrNull

        for (method in classDef.methods) {
            val match = matchOrNull(method, classDef, patchContext)
            if (match != null) {
                _matchOrNull = match
                return match
            }
        }
        return null
    }

    /**
     * Match using a [Method] (the class is resolved automatically).
     */
    fun matchOrNull(
        method: Method,
        patchContext: BytecodePatchContext,
    ): Match? {
        if (_matchOrNull != null) return _matchOrNull
        return matchOrNull(method, patchContext.classDefBy(method.definingClass), patchContext)
    }

    /**
     * Match using a [Method] and its [ClassDef].
     */
    fun matchOrNull(
        method: Method,
        classDef: ClassDef,
        patchContext: BytecodePatchContext,
    ): Match? {
        if (_matchOrNull != null) return _matchOrNull

        if (returnType != null && !method.returnType.startsWith(returnType)) return null
        if (accessFlags != null && accessFlags != method.accessFlags) return null
        if (parameters != null && !parametersStartsWith(method.parameterTypes, parameters)) return null
        if (custom != null && !custom.invoke(method, classDef)) return null

        // Legacy string declarations
        val stringMatches: List<Match.StringMatch>? = if (strings == null) {
            null
        } else {
            buildList {
                val instructions = method.instructionsOrNull ?: return null
                var remainingStrings: MutableList<String>? = null

                instructions.forEachIndexed { index, insn ->
                    if (insn.opcode != Opcode.CONST_STRING && insn.opcode != Opcode.CONST_STRING_JUMBO) return@forEachIndexed
                    val str = ((insn as ReferenceInstruction).reference as StringReference).string
                    if (remainingStrings == null) remainingStrings = strings.toMutableList()
                    val pos = remainingStrings.indexOfFirst(str::contains)
                    if (pos < 0) return@forEachIndexed
                    add(Match.StringMatch(str, index))
                    remainingStrings.removeAt(pos)
                }

                if (remainingStrings == null || remainingStrings.isNotEmpty()) return null
            }
        }

        val instructionMatches = if (filters == null) {
            null
        } else {
            val instructions = method.instructionsOrNull?.toList() ?: return null

            fun matchFilters(): List<Match.InstructionMatch>? {
                val lastIndex = instructions.lastIndex
                var matches: MutableList<Match.InstructionMatch>? = null
                var firstInstructionIndex = 0
                var lastMatchIndex = -1

                firstFilterLoop@ while (true) {
                    var firstFilterIdx = -1
                    var subIdx = firstInstructionIndex

                    for (filterIndex in filters.indices) {
                        val filter = filters[filterIndex]
                        val location = filter.location
                        var matched = false

                        while (subIdx <= lastIndex && location.indexIsValidForMatching(lastMatchIndex, subIdx)) {
                            val insn = instructions[subIdx]
                            if (filter.matches(method, insn)) {
                                lastMatchIndex = subIdx
                                if (filterIndex == 0) firstFilterIdx = subIdx
                                if (matches == null) matches = ArrayList(filters.size)
                                matches += Match.InstructionMatch(filter, subIdx, insn)
                                matched = true
                                subIdx++
                                break
                            }
                            subIdx++
                        }

                        if (!matched) {
                            if (filterIndex == 0) return null
                            firstInstructionIndex = firstFilterIdx + 1
                            matches?.clear()
                            continue@firstFilterLoop
                        }
                    }
                    return matches
                }
            }

            matchFilters() ?: return null
        }

        _matchOrNull = Match(
            patchContext,
            classDef,
            method,
            instructionMatches,
            stringMatches,
        )
        return _matchOrNull
    }

    fun patchException() = PatchException("Failed to match the fingerprint: $this")

    fun match(patchContext: BytecodePatchContext): Match =
        matchOrNull(patchContext) ?: throw patchException()

    fun match(classDef: ClassDef, patchContext: BytecodePatchContext): Match =
        matchOrNull(classDef, patchContext) ?: throw patchException()

    fun match(method: Method, patchContext: BytecodePatchContext): Match =
        matchOrNull(method, patchContext) ?: throw patchException()

    fun match(method: Method, classDef: ClassDef, patchContext: BytecodePatchContext): Match =
        matchOrNull(method, classDef, patchContext) ?: throw patchException()

    // Converted properties to functions
    fun originalClassDefOrNull(patchContext: BytecodePatchContext): ClassDef? =
        matchOrNull(patchContext)?.originalClassDef

    fun originalMethodOrNull(patchContext: BytecodePatchContext): Method? =
        matchOrNull(patchContext)?.originalMethod

    fun classDefOrNull(patchContext: BytecodePatchContext): ClassDef? =
        matchOrNull(patchContext)?.classDef

    fun methodOrNull(patchContext: BytecodePatchContext): Method? =
        matchOrNull(patchContext)?.method

    fun instructionMatchesOrNull(patchContext: BytecodePatchContext): List<Match.InstructionMatch>? =
        matchOrNull(patchContext)?.instructionMatchesOrNull

    fun originalClassDef(patchContext: BytecodePatchContext): ClassDef =
        match(patchContext).originalClassDef

    fun originalMethod(patchContext: BytecodePatchContext): Method =
        match(patchContext).originalMethod

    fun classDef(patchContext: BytecodePatchContext): ClassDef =
        match(patchContext).classDef

    fun method(patchContext: BytecodePatchContext): Method =
        match(patchContext).method

    fun instructionMatches(patchContext: BytecodePatchContext): List<Match.InstructionMatch> =
        match(patchContext).instructionMatches

    fun stringMatches(patchContext: BytecodePatchContext): List<Match.StringMatch> =
        match(patchContext).stringMatches

    fun stringMatchesOrNull(patchContext: BytecodePatchContext): List<Match.StringMatch>? =
        matchOrNull(patchContext)?.stringMatchesOrNull
}

/**
 * A match of a [Fingerprint].
 *
 * @param patchContext The bytecode patch context.
 * @param originalClassDef The class the matching method is a member of.
 * @param originalMethod The matching method.
 * @param _instructionMatches The match for the instruction filters.
 * @param _stringMatches The matches for the strings declared using `strings()`.
 */
class Match internal constructor(
    private val patchContext: BytecodePatchContext,
    val originalClassDef: ClassDef,
    val originalMethod: Method,
    private val _instructionMatches: List<InstructionMatch>?,
    private val _stringMatches: List<StringMatch>?,
) {
    val classDef by lazy { patchContext.mutableClassDefBy(originalClassDef) }

    val method by lazy {
        classDef.methods.first { MethodUtil.methodSignaturesMatch(it, originalMethod) }
    }

    val instructionMatches: List<InstructionMatch>
        get() = _instructionMatches ?: throw PatchException("Fingerprint declared no instruction filters")
    val instructionMatchesOrNull: List<InstructionMatch>? = _instructionMatches

    class InstructionMatch internal constructor(
        val filter: InstructionFilter,
        val index: Int,
        val instruction: Instruction
    ) {
        @Suppress("UNCHECKED_CAST")
        fun <T> getInstruction(): T = instruction as T

        override fun toString(): String =
            "InstructionMatch{filter='${filter.javaClass.simpleName}', opcode='${instruction.opcode}', index=$index}"
    }

    val stringMatches: List<StringMatch>
        get() = _stringMatches ?: throw PatchException("Fingerprint declared no strings")
    val stringMatchesOrNull: List<StringMatch>? = _stringMatches

    class StringMatch internal constructor(val string: String, val index: Int)
}

/**
 * A builder for [Fingerprint].
 */
class FingerprintBuilder() {
    private var accessFlags: Int? = null
    private var returnType: String? = null
    private var parameters: List<String>? = null
    private var instructionFilters: List<InstructionFilter>? = null
    private var strings: List<String>? = null
    private var customBlock: ((method: Method, classDef: ClassDef) -> Boolean)? = null

    fun accessFlags(accessFlags: Int) {
        this.accessFlags = accessFlags
    }

    fun accessFlags(vararg accessFlags: AccessFlags) {
        this.accessFlags = accessFlags.fold(0) { acc, it -> acc or it.value }
    }

    fun returns(returnType: String) {
        this.returnType = returnType
    }

    fun parameters(vararg parameters: String) {
        this.parameters = parameters.toList()
    }

    private fun verifyNoFiltersSet() {
        if (this.instructionFilters != null) {
            throw PatchException("Instruction filters already set")
        }
    }

    fun opcodes(vararg opcodes: Opcode?) {
        verifyNoFiltersSet()
        if (opcodes.isEmpty()) throw IllegalArgumentException("One or more opcodes is required")
        this.instructionFilters = OpcodesFilter.listOfOpcodes(opcodes.toList())
    }

    fun opcodes(instructions: String) {
        verifyNoFiltersSet()
        if (instructions.isBlank()) throw IllegalArgumentException("No instructions declared (empty string)")
        this.instructionFilters = OpcodesFilter.listOfOpcodes(
            instructions.trimIndent().split("\n").filter { it.isNotBlank() }.map {
                val name = it.split(" ", limit = 1).first().trim()
                if (name == "null") return@map null
                opcodesByName[name] ?: throw IllegalArgumentException("Unknown opcode: $name")
            }
        )
    }

    fun instructions(vararg instructionFilters: InstructionFilter) {
        verifyNoFiltersSet()
        if (instructionFilters.isEmpty()) throw IllegalArgumentException("One or more instructions is required")
        this.instructionFilters = instructionFilters.toList()
    }

    @Deprecated("Instead use `instruction()` filters and `string()` instruction declarations")
    fun strings(vararg strings: String) {
        this.strings = strings.toList()
    }

    fun custom(customBlock: (method: Method, classDef: ClassDef) -> Boolean) {
        this.customBlock = customBlock
    }

    internal fun build(): Fingerprint {
        if (returnType?.equals("V") == true && accessFlags != null && AccessFlags.CONSTRUCTOR.isSet(accessFlags!!)) {
            returnType = null
        }
        return Fingerprint(
            accessFlags,
            returnType,
            parameters,
            instructionFilters,
            strings,
            customBlock,
        )
    }

    private companion object {
        val opcodesByName = Opcode.entries.associateBy { it.name }
    }
}

fun fingerprint(
    block: FingerprintBuilder.() -> Unit,
) = FingerprintBuilder().apply(block).build()

@Deprecated("Opcode pattern fuzzy matching never worked well and has been removed")
fun fingerprint(
    fuzzyPatternScanThreshold: Int = 0,
    block: FingerprintBuilder.() -> Unit,
) = fingerprint(block)

internal fun parametersStartsWith(
    targetMethodParameters: Iterable<CharSequence>,
    fingerprintParameters: Iterable<CharSequence>,
): Boolean {
    if (fingerprintParameters.count() != targetMethodParameters.count()) return false
    val fingerprintIterator = fingerprintParameters.iterator()
    targetMethodParameters.forEach {
        if (!it.startsWith(fingerprintIterator.next())) return false
    }
    return true
}