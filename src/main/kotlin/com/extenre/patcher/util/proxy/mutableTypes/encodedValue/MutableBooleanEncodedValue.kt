/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.base.value.BaseBooleanEncodedValue
import com.android.tools.smali.dexlib2.iface.value.BooleanEncodedValue

class MutableBooleanEncodedValue(booleanEncodedValue: BooleanEncodedValue) : BaseBooleanEncodedValue(),
    MutableEncodedValue {
    private var value = booleanEncodedValue.value

    override fun getValue(): Boolean {
        return this.value
    }

    fun setValue(value: Boolean) {
        this.value = value
    }

    companion object {
        fun BooleanEncodedValue.toMutable(): MutableBooleanEncodedValue {
            return MutableBooleanEncodedValue(this)
        }
    }
}