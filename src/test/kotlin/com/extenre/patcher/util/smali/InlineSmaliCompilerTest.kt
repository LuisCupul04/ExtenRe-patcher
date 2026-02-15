/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.smali

import com.extenre.patcher.extensions.addInstructions
import com.extenre.patcher.extensions.instruction
import com.extenre.patcher.extensions.label
import com.extenre.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.BuilderInstruction
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21t
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class InlineSmaliCompilerTest {
    @Test
    fun `compiler should output valid instruction`() {
        val want = BuilderInstruction21c(Opcode.CONST_STRING, 0, ImmutableStringReference("Test")) as BuilderInstruction
        val have = "const-string v0, \"Test\"".toInstruction()
        instructionEquals(want, have)
    }

    @Test
    fun `compiler should support branching with own branches`() {
        val method = createMethod()
        val insnAmount = 8
        val insnIndex = insnAmount - 2
        val targetIndex = insnIndex - 1

        method.addInstructions(arrayOfNulls<String>(insnAmount).also {
            Arrays.fill(it, "const/4 v0, 0x0")
        }.joinToString("\n"))
        method.addInstructions(
            targetIndex,
            """
                :test
                const/4 v0, 0x1
                if-eqz v0, :test
            """
        )

        val insn = method.instruction<BuilderInstruction21t>(insnIndex)
        assertEquals(targetIndex, insn.target.location.index)
    }

    @Test
    fun `compiler should support branching to outside branches`() {
        val method = createMethod()
        val insnIndex = 3
        val labelIndex = 1

        method.addInstructions(
            """
                const/4 v0, 0x1
                const/4 v0, 0x0
            """
        )

        assertEquals(labelIndex, method.label(labelIndex).location.index)

        method.addInstructions(
            """
                const/4 v0, 0x1
                if-eqz v0, :test
                return-void
            """, listOf(
                ExternalLabel("test",method.instruction(1))
            )
        )

        val insn = method.instruction<BuilderInstruction21t>(insnIndex)
        assertTrue(insn.target.isPlaced, "Label was not placed")
        assertEquals(labelIndex, insn.target.location.index)
    }

    companion object {
        private fun createMethod(
            name: String = "dummy",
            returnType: String = "V",
            accessFlags: Int = AccessFlags.STATIC.value,
            registerCount: Int = 1,
        ) = ImmutableMethod(
            "Ldummy;",
            name,
            emptyList(), // parameters
            returnType,
            accessFlags,
            emptySet(),
            emptySet(),
            MutableMethodImplementation(registerCount)
        ).toMutable()

        private fun instructionEquals(want: BuilderInstruction, have: BuilderInstruction) {
            assertEquals(want.opcode, have.opcode)
            assertEquals(want.format, have.format)
            assertEquals(want.codeUnits, have.codeUnits)
        }
    }
}