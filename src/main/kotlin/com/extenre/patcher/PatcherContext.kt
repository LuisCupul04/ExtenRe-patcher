/*
 * Copyright (C) 2022 ReVanced LLC
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2026 LuisCupul04
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.extenre.patcher

import com.extenre.patcher.patch.BytecodePatchContext
import com.extenre.patcher.patch.Patch
import com.extenre.patcher.patch.ResourcePatchContext
import brut.androlib.apk.ApkInfo
import brut.directory.ExtFile
import java.io.Closeable

/**
 * A context for the patcher containing the current state of the patcher.
 *
 * @param config The configuration for the patcher.
 */
@Suppress("MemberVisibilityCanBePrivate")
class PatcherContext internal constructor(config: PatcherConfig): Closeable {
    /**
     * [PackageMetadata] of the supplied [PatcherConfig.apkFile].
     */
    val packageMetadata = PackageMetadata(ApkInfo(ExtFile(config.apkFile)))

    /**
     * The set of [Patch]es.
     */
    internal val executablePatches = mutableSetOf<Patch<*>>()

    /**
     * The set of all [Patch]es and their dependencies.
     */
    internal val allPatches = mutableSetOf<Patch<*>>()

    /**
     * The context for patches containing the current state of the resources.
     */
    internal val resourceContext = ResourcePatchContext(packageMetadata, config)

    /**
     * The context for patches containing the current state of the bytecode.
     */
    internal val bytecodeContext = BytecodePatchContext(config)

    override fun close() = bytecodeContext.close()
}
