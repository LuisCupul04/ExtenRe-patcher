/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher

import com.extenre.patcher.apk.ApkBundle
import com.extenre.patcher.logging.Logger

/**
 * Options for the [Patcher].
 * @param apkBundle The [ApkBundle].
 * @param logger Custom logger implementation for the [Patcher].
 */
class PatcherOptions(
    internal val apkBundle: ApkBundle,
    internal val logger: Logger = Logger.Nop
)