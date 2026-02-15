/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.usage.resource.annotation

import com.extenre.patcher.annotation.Compatibility
import com.extenre.patcher.annotation.Package

@Compatibility(
    [Package(
        "com.example.examplePackage", arrayOf("0.0.1", "0.0.2")
    )]
)
@Target(AnnotationTarget.CLASS)
internal annotation class ExampleResourceCompatibility

