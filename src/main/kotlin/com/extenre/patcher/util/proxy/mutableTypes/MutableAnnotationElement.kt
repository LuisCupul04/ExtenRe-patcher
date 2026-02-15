/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util.proxy.mutableTypes

import com.extenre.patcher.util.proxy.mutableTypes.encodedValue.MutableEncodedValue
import com.extenre.patcher.util.proxy.mutableTypes.encodedValue.MutableEncodedValue.Companion.toMutable
import com.android.tools.smali.dexlib2.base.BaseAnnotationElement
import com.android.tools.smali.dexlib2.iface.AnnotationElement
import com.android.tools.smali.dexlib2.iface.value.EncodedValue

class MutableAnnotationElement(annotationElement: AnnotationElement) : BaseAnnotationElement() {
    private var name = annotationElement.name
    private var value = annotationElement.value.toMutable()

    fun setName(name: String) {
        this.name = name
    }

    fun setValue(value: MutableEncodedValue) {
        this.value = value
    }

    override fun getName(): String {
        return name
    }

    override fun getValue(): EncodedValue {
        return value
    }

    companion object {
        fun AnnotationElement.toMutable(): MutableAnnotationElement {
            return MutableAnnotationElement(this)
        }
    }
}