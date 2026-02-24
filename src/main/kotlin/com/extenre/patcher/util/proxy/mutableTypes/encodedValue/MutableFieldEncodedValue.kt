/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.ValueType
import com.android.tools.smali.dexlib2.base.value.BaseFieldEncodedValue
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.value.FieldEncodedValue

class MutableFieldEncodedValue(fieldEncodedValue: FieldEncodedValue) :
    BaseFieldEncodedValue(),
    MutableEncodedValue {
    private var value = fieldEncodedValue.value

    override fun getValueType(): Int = ValueType.FIELD

    override fun getValue(): FieldReference = this.value

    fun setValue(value: FieldReference) {
        this.value = value
    }

    companion object {
        fun FieldEncodedValue.toMutable(): MutableFieldEncodedValue = MutableFieldEncodedValue(this)
    }
}
