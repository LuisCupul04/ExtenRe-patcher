/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.base.value.BaseShortEncodedValue
import com.android.tools.smali.dexlib2.iface.value.ShortEncodedValue

class MutableShortEncodedValue(shortEncodedValue: ShortEncodedValue) : BaseShortEncodedValue(), MutableEncodedValue {
    private var value = shortEncodedValue.value

    override fun getValue(): Short {
        return this.value
    }

    fun setValue(value: Short) {
        this.value = value
    }

    companion object {
        fun ShortEncodedValue.toMutable(): MutableShortEncodedValue {
            return MutableShortEncodedValue(this)
        }
    }
}
