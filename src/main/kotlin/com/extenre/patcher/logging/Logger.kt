/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.logging
interface Logger {
    fun error(msg: String) {}
    fun warn(msg: String) {}
    fun info(msg: String) {}
    fun trace(msg: String) {}

    object Nop : Logger
}