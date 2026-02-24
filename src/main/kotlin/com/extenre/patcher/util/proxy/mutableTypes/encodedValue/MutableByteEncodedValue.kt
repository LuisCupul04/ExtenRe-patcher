/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes.encodedValue

import com.android.tools.smali.dexlib2.base.value.BaseByteEncodedValue
import com.android.tools.smali.dexlib2.iface.value.ByteEncodedValue

class MutableByteEncodedValue(byteEncodedValue: ByteEncodedValue) :
    BaseByteEncodedValue(),
    MutableEncodedValue {
    private var value = byteEncodedValue.value

    override fun getValue(): Byte = this.value

    fun setValue(value: Byte) {
        this.value = value
    }

    companion object {
        fun ByteEncodedValue.toMutable(): MutableByteEncodedValue = MutableByteEncodedValue(this)
    }
}
