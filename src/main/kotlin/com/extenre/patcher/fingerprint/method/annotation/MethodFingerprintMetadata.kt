/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.fingerprint.method.annotation

import com.extenre.patcher.fingerprint.method.impl.MethodFingerprint

/**
 * Annotations to scan a pattern [MethodFingerprint] with fuzzy algorithm.
 * @param threshold if [threshold] or more of the opcodes do not match, skip.
 */
@Target(AnnotationTarget.CLASS)
annotation class FuzzyPatternScanMethod(
    val threshold: Int = 1
)