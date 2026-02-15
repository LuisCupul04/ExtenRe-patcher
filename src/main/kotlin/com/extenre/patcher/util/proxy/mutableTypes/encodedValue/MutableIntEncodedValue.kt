/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.base.value.BaseIntEncodedValue
import com.android.tools.smali.dexlib2.iface.value.IntEncodedValue

class MutableIntEncodedValue(intEncodedValue: IntEncodedValue) : BaseIntEncodedValue(), MutableEncodedValue {
    private var value = intEncodedValue.value

    override fun getValue(): Int {
        return this.value
    }

    fun setValue(value: Int) {
        this.value = value
    }

    companion object {
        fun IntEncodedValue.toMutable(): MutableIntEncodedValue {
            return MutableIntEncodedValue(this)
        }
    }
}