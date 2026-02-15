/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.base.value.BaseCharEncodedValue
import com.android.tools.smali.dexlib2.iface.value.CharEncodedValue

class MutableCharEncodedValue(charEncodedValue: CharEncodedValue) : BaseCharEncodedValue(), MutableEncodedValue {
    private var value = charEncodedValue.value

    override fun getValue(): Char {
        return this.value
    }

    fun setValue(value: Char) {
        this.value = value
    }

    companion object {
        fun CharEncodedValue.toMutable(): MutableCharEncodedValue {
            return MutableCharEncodedValue(this)
        }
    }
}
