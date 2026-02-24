/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.base.value.BaseMethodEncodedValue
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.value.MethodEncodedValue

class MutableMethodEncodedValue(methodEncodedValue: MethodEncodedValue) :
    BaseMethodEncodedValue(),
    MutableEncodedValue {
    private var value = methodEncodedValue.value

    override fun getValue(): MethodReference = this.value

    fun setValue(value: MethodReference) {
        this.value = value
    }

    companion object {
        fun MethodEncodedValue.toMutable(): MutableMethodEncodedValue = MutableMethodEncodedValue(this)
    }
}
