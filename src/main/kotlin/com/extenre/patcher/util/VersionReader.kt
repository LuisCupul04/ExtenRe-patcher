/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util

import java.util.*

internal object VersionReader {
    @JvmStatic
    private val properties = Properties().apply {
        load(
            VersionReader::class.java.getResourceAsStream("/com/extenre/patcher/version.properties")
                ?: throw IllegalStateException("Could not load version.properties")
        )
    }

    @JvmStatic
    fun read(): String {
        return properties.getProperty("version") ?: throw IllegalStateException("Version not found")
    }
}