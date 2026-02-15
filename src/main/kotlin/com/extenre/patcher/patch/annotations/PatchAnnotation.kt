/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.patch.annotations

import com.extenre.patcher.Context
import com.extenre.patcher.patch.Patch
import kotlin.reflect.KClass

/**
 * Annotation to mark a class as a patch.
 * @param include If false, the patch should be treated as optional by default.
 */
@Target(AnnotationTarget.CLASS)
annotation class Patch(val include: Boolean = true)

/**
 * Annotation for dependencies of [Patch]es.
 */
@Target(AnnotationTarget.CLASS)
annotation class DependsOn(
    val dependencies: Array<KClass<out Patch<Context>>> = []
)


/**
 * Annotation to mark [Patch]es which depend on integrations.
 */
@Target(AnnotationTarget.CLASS)
annotation class RequiresIntegrations // required because integrations are decoupled from patches