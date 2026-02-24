/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util

import com.android.tools.smali.dexlib2.iface.ClassDef

@Deprecated("Instead use PatchClasses", ReplaceWith("PatchClasses"))
class ProxyClassList internal constructor(classes: MutableList<ClassDef>) : MutableList<ClassDef> by classes
