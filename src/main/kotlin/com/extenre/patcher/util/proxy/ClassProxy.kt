/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy

import com.extenre.patcher.util.PatchClasses
import com.android.tools.smali.dexlib2.iface.ClassDef

@Deprecated("Instead use BytecodePatchContext class lookup methods")
class ClassProxy internal constructor(
    val immutableClass: ClassDef,
    patchClasses: PatchClasses
) {
    @Deprecated("Instead use BytecodePatchContext class lookup methods")
    val mutableClass by lazy {
        patchClasses.mutableClassBy(immutableClass)
    }
}
