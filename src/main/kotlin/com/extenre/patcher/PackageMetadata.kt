/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher

import brut.androlib.apk.ApkInfo

/**
 * Metadata about a package.
 *
 * @param apkInfo The [ApkInfo] of the apk file.
 */
class PackageMetadata internal constructor(internal val apkInfo: ApkInfo) {
    lateinit var packageName: String
        internal set

    lateinit var packageVersion: String
        internal set
}
