/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.base.value.BaseMethodHandleEncodedValue
import com.android.tools.smali.dexlib2.iface.reference.MethodHandleReference
import com.android.tools.smali.dexlib2.iface.value.MethodHandleEncodedValue

class MutableMethodHandleEncodedValue(methodHandleEncodedValue: MethodHandleEncodedValue) :
    BaseMethodHandleEncodedValue(),
    MutableEncodedValue {
    private var value = methodHandleEncodedValue.value

    override fun getValue(): MethodHandleReference = this.value

    fun setValue(value: MethodHandleReference) {
        this.value = value
    }

    companion object {
        fun MethodHandleEncodedValue.toMutable(): MutableMethodHandleEncodedValue = MutableMethodHandleEncodedValue(this)
    }
}
