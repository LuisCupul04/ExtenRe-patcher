/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.usage.resource.patch

import com.extenre.patcher.ResourceContext
import com.extenre.patcher.annotation.Description
import com.extenre.patcher.annotation.Name
import com.extenre.patcher.annotation.Version
import com.extenre.patcher.apk.Apk
import com.extenre.patcher.patch.ResourcePatch
import com.extenre.patcher.patch.annotations.Patch
import com.extenre.patcher.usage.resource.annotation.ExampleResourceCompatibility
import org.w3c.dom.Element

@Patch
@Name("example-resource-patch")
@Description("Example demonstration of a resource patch.")
@ExampleResourceCompatibility
@Version("0.0.1")
class ExampleResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        context.apkBundle.base.resources.openXmlFile(Apk.manifest).use { editor ->
            val element = editor // regular DomFileEditor
                .file
                .getElementsByTagName("application")
                .item(0) as Element
            element
                .setAttribute(
                    "exampleAttribute",
                    "exampleValue"
                )
        }
    }
}