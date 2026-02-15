/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.smali

import com.android.tools.smali.dexlib2.iface.instruction.Instruction

/**
 * A class that represents a label for an instruction.
 * @param name The label name.
 * @param instruction The instruction that this label is for.
 */
data class ExternalLabel(internal val name: String, internal val instruction: Instruction)
