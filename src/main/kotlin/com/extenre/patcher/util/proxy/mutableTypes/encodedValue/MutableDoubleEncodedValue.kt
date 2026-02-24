/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.base.value.BaseDoubleEncodedValue
import com.android.tools.smali.dexlib2.iface.value.DoubleEncodedValue

class MutableDoubleEncodedValue(doubleEncodedValue: DoubleEncodedValue) :
    BaseDoubleEncodedValue(),
    MutableEncodedValue {
    private var value = doubleEncodedValue.value

    override fun getValue(): Double = this.value

    fun setValue(value: Double) {
        this.value = value
    }

    companion object {
        fun DoubleEncodedValue.toMutable(): MutableDoubleEncodedValue = MutableDoubleEncodedValue(this)
    }
}
