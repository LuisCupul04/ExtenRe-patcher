/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.extensions

import com.extenre.patcher.util.proxy.mutableTypes.MutableMethod

/**
 * Create a label for the instruction at given index.
 *
 * @param index The index to create the label for the instruction at.
 * @return The label.
 */
fun MutableMethod.newLabel(index: Int) = implementation!!.newLabelForIndex(index)
