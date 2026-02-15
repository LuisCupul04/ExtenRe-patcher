/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.extensions

import com.extenre.patcher.annotation.Compatibility
import com.extenre.patcher.annotation.Description
import com.extenre.patcher.annotation.Name
import com.extenre.patcher.annotation.Version
import com.extenre.patcher.fingerprint.method.annotation.FuzzyPatternScanMethod
import com.extenre.patcher.fingerprint.method.impl.MethodFingerprint
import com.extenre.patcher.patch.OptionsContainer
import com.extenre.patcher.patch.PatchClass
import com.extenre.patcher.patch.PatchOptions
import com.extenre.patcher.patch.annotations.DependsOn
import com.extenre.patcher.patch.annotations.RequiresIntegrations
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance

/**
 * Recursively find a given annotation on a class.
 * @param targetAnnotation The annotation to find.
 * @return The annotation.
 */
private fun <T : Annotation> Class<*>.findAnnotationRecursively(targetAnnotation: KClass<T>) =
    this.findAnnotationRecursively(targetAnnotation.java, mutableSetOf())


private fun <T : Annotation> Class<*>.findAnnotationRecursively(
    targetAnnotation: Class<T>, traversed: MutableSet<Annotation>
): T? {
    val found = this.annotations.firstOrNull { it.annotationClass.java.name == targetAnnotation.name }

    @Suppress("UNCHECKED_CAST") if (found != null) return found as T

    for (annotation in this.annotations) {
        if (traversed.contains(annotation)) continue
        traversed.add(annotation)

        return (annotation.annotationClass.java.findAnnotationRecursively(targetAnnotation, traversed)) ?: continue
    }

    return null
}

object PatchExtensions {
    val PatchClass.patchName: String
        get() = findAnnotationRecursively(Name::class)?.name ?: this.simpleName

    val PatchClass.version
        get() = findAnnotationRecursively(Version::class)?.version

    val PatchClass.include
        get() = findAnnotationRecursively(com.extenre.patcher.patch.annotations.Patch::class)!!.include

    val PatchClass.description
        get() = findAnnotationRecursively(Description::class)?.description

    val PatchClass.dependencies
        get() = findAnnotationRecursively(DependsOn::class)?.dependencies

    val PatchClass.compatiblePackages
        get() = findAnnotationRecursively(Compatibility::class)?.compatiblePackages

    internal val PatchClass.requiresIntegrations
        get() = findAnnotationRecursively(RequiresIntegrations::class) != null

    val PatchClass.options: PatchOptions?
        get() = kotlin.companionObject?.let { cl ->
            if (cl.visibility != KVisibility.PUBLIC) return null
            kotlin.companionObjectInstance?.let {
                (it as? OptionsContainer)?.options
            }
        }
}

object MethodFingerprintExtensions {
    val MethodFingerprint.name: String
        get() = this.javaClass.simpleName

    val MethodFingerprint.fuzzyPatternScanMethod
        get() = javaClass.findAnnotationRecursively(FuzzyPatternScanMethod::class)

    val MethodFingerprint.fuzzyScanThreshold
        get() = fuzzyPatternScanMethod?.threshold ?: 0
}
