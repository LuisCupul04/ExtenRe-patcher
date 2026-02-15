/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.extenre.patcher.util.proxy.mutableTypes.encodedValue.MutableEncodedValue.Companion.toMutable
import com.android.tools.smali.dexlib2.base.value.BaseArrayEncodedValue
import com.android.tools.smali.dexlib2.iface.value.ArrayEncodedValue
import com.android.tools.smali.dexlib2.iface.value.EncodedValue

class MutableArrayEncodedValue(arrayEncodedValue: ArrayEncodedValue) : BaseArrayEncodedValue(), MutableEncodedValue {
    private val _value by lazy {
        arrayEncodedValue.value.map { encodedValue -> encodedValue.toMutable() }.toMutableList()
    }

    override fun getValue(): MutableList<out EncodedValue> {
        return _value
    }

    companion object {
        fun ArrayEncodedValue.toMutable(): MutableArrayEncodedValue {
            return MutableArrayEncodedValue(this)
        }
    }
}
