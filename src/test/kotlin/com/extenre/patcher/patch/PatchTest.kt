/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher.patch

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal object PatchTest {
    @Test
    fun `can create patch with name`() {
        val patch = bytecodePatch(name = "Test") {}

        assertEquals("Test", patch.name)
    }

    @Test
    fun `can create patch with compatible packages`() {
        val patch = bytecodePatch(name = "Test") {
            compatibleWith(
                "compatible.package"("1.0.0"),
            )
        }

        val packages = patch.compatiblePackages
        assertNotNull(packages, "compatiblePackages should not be null")
        assertEquals(1, packages.size)
        assertEquals("compatible.package", packages.first().first)
    }

    @Test
    fun `can create patch with dependencies`() {
        val patch = bytecodePatch(name = "Test") {
            dependsOn(resourcePatch {})
        }

        assertEquals(1, patch.dependencies.size)
    }

    @Test
    fun `can create patch with options`() {
        val patch = bytecodePatch(name = "Test") {
            val print by stringOption("print")
            val custom = option<String>("custom")()

            execute {
                println(print)
                println(custom.value)
            }
        }

        assertEquals(2, patch.options.size)
    }
}