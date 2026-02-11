package com.extenre.patcher.util

import java.util.*

internal object VersionReader {
    @JvmStatic
    private val properties = Properties().apply {
        load(
            VersionReader::class.java.getResourceAsStream("/com/extenre/patcher/version.properties")
                ?: throw IllegalStateException("Could not load version.properties")
        )
    }

    @JvmStatic
    fun read(): String {
        return properties.getProperty("version") ?: throw IllegalStateException("Version not found")
    }
}