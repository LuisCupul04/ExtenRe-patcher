/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.patch

/**
 * A container for patch options.
 */
abstract class OptionsContainer {
    /**
     * A list of [PatchOption]s.
     * @see PatchOptions
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val options = PatchOptions()

    protected fun <T> option(opt: PatchOption<T>): PatchOption<T> {
        options.register(opt)
        return opt
    }
}