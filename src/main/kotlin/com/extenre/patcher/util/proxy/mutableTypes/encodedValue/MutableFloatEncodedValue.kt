/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.base.value.BaseFloatEncodedValue
import com.android.tools.smali.dexlib2.iface.value.FloatEncodedValue

class MutableFloatEncodedValue(floatEncodedValue: FloatEncodedValue) : BaseFloatEncodedValue(), MutableEncodedValue {
    private var value = floatEncodedValue.value

    override fun getValue(): Float {
        return this.value
    }

    fun setValue(value: Float) {
        this.value = value
    }

    companion object {
        fun FloatEncodedValue.toMutable(): MutableFloatEncodedValue {
            return MutableFloatEncodedValue(this)
        }
    }
}