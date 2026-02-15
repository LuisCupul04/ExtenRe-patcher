/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.util

import com.extenre.patcher.BytecodeContext
import com.extenre.patcher.util.proxy.mutableTypes.MutableClass

object TypeUtil {
    /**
     * Traverse the class hierarchy starting from the given root class.
     *
     * @param targetClass The class to start traversing the class hierarchy from.
     * @param callback The function that is called for every class in the hierarchy.
     */
    fun BytecodeContext.traverseClassHierarchy(targetClass: MutableClass, callback: MutableClass.() -> Unit) {
        callback(targetClass)
        this.classes.findClassProxied(targetClass.superclass ?: return)?.mutableClass?.let {
            traverseClassHierarchy(it, callback)
        }
    }
}
