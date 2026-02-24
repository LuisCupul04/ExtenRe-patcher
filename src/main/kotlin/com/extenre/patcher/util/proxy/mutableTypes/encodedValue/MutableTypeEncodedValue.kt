/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.base.value.BaseTypeEncodedValue
import com.android.tools.smali.dexlib2.iface.value.TypeEncodedValue

class MutableTypeEncodedValue(typeEncodedValue: TypeEncodedValue) :
    BaseTypeEncodedValue(),
    MutableEncodedValue {
    private var value = typeEncodedValue.value

    override fun getValue(): String = this.value

    fun setValue(value: String) {
        this.value = value
    }

    companion object {
        fun TypeEncodedValue.toMutable(): MutableTypeEncodedValue = MutableTypeEncodedValue(this)
    }
}
