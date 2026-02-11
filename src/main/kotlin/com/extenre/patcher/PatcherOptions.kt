package com.extenre.patcher

import com.extenre.patcher.apk.ApkBundle
import com.extenre.patcher.logging.Logger

/**
 * Options for the [Patcher].
 * @param apkBundle The [ApkBundle].
 * @param logger Custom logger implementation for the [Patcher].
 */
class PatcherOptions(
    internal val apkBundle: ApkBundle,
    internal val logger: Logger = Logger.Nop
)