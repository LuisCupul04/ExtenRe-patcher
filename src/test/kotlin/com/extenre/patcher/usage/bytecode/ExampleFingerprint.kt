/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.usage.bytecode
import com.extenre.patcher.extensions.or
import com.extenre.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import com.extenre.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

@FuzzyPatternScanMethod(2)
object ExampleFingerprint : MethodFingerprint(
    "V",
    AccessFlags.PUBLIC or AccessFlags.STATIC,
    listOf("[L"),
    listOf(
        Opcode.SGET_OBJECT,
        null,                 // Testing unknown opcodes.
        Opcode.INVOKE_STATIC, // This is intentionally wrong to test the Fuzzy resolver.
        Opcode.RETURN_VOID
    ),
    null
)