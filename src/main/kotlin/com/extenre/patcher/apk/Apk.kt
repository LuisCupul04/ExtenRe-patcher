/*
 * Copyright (C) 2026 LuisCupul04
 * Copyright (C) 2022 inotia00
 * Copyright (C) 2022 ReVanced LLC
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.extenre.patcher.apk

import com.extenre.patcher.DomFileEditor
import com.extenre.patcher.Patcher
import com.extenre.patcher.PatcherOptions
import com.extenre.patcher.resource.*
import com.extenre.patcher.util.ProxyBackedClassList
import com.extenre.patcher.util.xml.LazyXMLInputSource
import com.reandroid.apk.ApkModule
import com.reandroid.apk.xmlencoder.EncodeException
import com.reandroid.apk.xmlencoder.EncodeMaterials
import com.reandroid.apk.xmlencoder.EncodeUtil
import com.reandroid.archive.InputSource
import com.reandroid.arsc.chunk.PackageBlock
import com.reandroid.arsc.chunk.PackageBlock.*
import com.reandroid.arsc.chunk.TableBlock
import com.reandroid.arsc.chunk.xml.AndroidManifestBlock
import com.reandroid.arsc.coder.ValueDecoder
import com.reandroid.arsc.value.Entry
import com.reandroid.arsc.value.ResConfig
import com.reandroid.xml.XMLDocument
import com.reandroid.xml.XMLSpannable
import lanchon.multidexlib2.BasicDexEntry
import lanchon.multidexlib2.DexIO
import lanchon.multidexlib2.MultiDexContainerBackedDexFile
import lanchon.multidexlib2.MultiDexIO
import lanchon.multidexlib2.RawDexIO
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.DexFile
import com.android.tools.smali.dexlib2.iface.MultiDexContainer
import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry


/**
 * An [Apk] file.
 */
sealed class Apk private constructor(internal val module: ApkModule) {
    companion object {
        const val manifest = "AndroidManifest.xml"
    }

    /**
     * The metadata of the [Apk].
     */
    val packageMetadata = PackageMetadata(module.androidManifestBlock)

    internal val archive = Archive(module)

    /**
     * Update the [PackageBlock] name to match the manifest.
     */
    protected open fun updatePackageBlock() {
        resources.packageBlock!!.name = module.apkArchive.getInputSource(manifest).openStream()
            .use { stream -> AndroidManifestBlock.load(stream) }.packageName
    }

    /**
     * Write the [Apk] to a file.
     *
     * @param options The [PatcherOptions] of the [Patcher].
     * @param file The target file.
     */
    internal open fun write(options: PatcherOptions, file: File) {
        archive.openFiles().forEach {
            options.logger.warn("${it.handle.virtualPath} was never closed!")
            it.close()
        }

        resources.useMaterials {
            module.apkArchive.listInputSources().filterIsInstance<LazyXMLInputSource>()
                .forEach(LazyXMLInputSource::encode)
        }

        updatePackageBlock()

        module.writeApk(file)
    }

    inner class ResourceContainer(internal val tableBlock: TableBlock?) {
        internal val packageBlock: PackageBlock? =
            tableBlock?.packageArray?.let { array ->
                if (array.childes.size == 1) array[0] else array.iterator()?.asSequence()
                    ?.single { it.name == module.packageName }
            }

        internal lateinit var global: ApkBundle.GlobalResources

        internal fun <R> useMaterials(callback: (EncodeMaterials) -> R): R {
            val materials = global.encodeMaterials
            val previous = materials.currentPackage
            if (packageBlock != null) {
                materials.currentPackage = packageBlock
            }

            return try {
                callback(materials)
            } finally {
                materials.currentPackage = previous
            }
        }

        internal fun expectPackageBlock() = packageBlock ?: throw ApkException.MissingResourceTable

        internal fun resolve(ref: String) = try {
            useMaterials { it.resolveReference(ref) }
        } catch (e: EncodeException) {
            throw ApkException.InvalidReference(ref, e)
        }

        private fun Entry.setTo(value: Resource) {
            // Preserve the entry name by restoring the spec reference.
            val specRef = specReference
            ensureComplex(value.complex)
            specReference = specRef

            value.write(this, this@ResourceContainer)
        }

        private fun getEntry(type: String, name: String, qualifiers: String?) =
            global.resTable.getResourceId(type, name)?.let { id ->
                val config = ResConfig.parse(qualifiers)
                tableBlock?.resolveReference(id)?.singleOrNull { it.resConfig == config }
            }

        private fun createHandle(resPath: String): ResourceFile.Handle {
            if (resPath.startsWith("res/values")) throw ApkException.Decode("Decoding the resource table as a file is not supported")

            var callback = {}
            var archivePath = resPath

            if (tableBlock != null && resPath.startsWith("res/") && resPath.count { it == '/' } == 2) {
                val file = File(resPath)

                val qualifiers = EncodeUtil.getQualifiersFromResFile(file)
                val type = EncodeUtil.getTypeNameFromResFile(file)
                val name = file.nameWithoutExtension

                // The resource file names that app developers use might not be kept in the archive, so we have to resolve it with the resource table.
                // Example: res/drawable-hdpi/icon.png -> res/4a.png
                val resolvedPath = getEntry(type, name, qualifiers)?.resValue?.valueAsString

                if (resolvedPath != null) {
                    archivePath = resolvedPath
                } else {
                    // An entry for this specific resource file was not found in the resource table, so we have to register it after we save.
                    callback = { set(type, name, StringResource(archivePath), qualifiers) }
                }
            }

            return ResourceFile.Handle(resPath, archivePath, callback)
        }

        /**
         * Create or update an Android resource.
         *
         * @param type The resource type.
         * @param name The name of the resource.
         * @param value The resource data.
         * @param configuration The resource configuration.
         */
        fun set(type: String, name: String, value: Resource, configuration: String? = null) =
            expectPackageBlock().getOrCreate(configuration, type, name).also { it.setTo(value) }.resourceId

        /**
         * Create or update multiple resources in an ARSC type block.
         *
         * @param type The resource type.
         * @param map A map of resource names to the corresponding value.
         * @param configuration The resource configuration.
         */
        fun setGroup(type: String, map: Map<String, Resource>, configuration: String? = null) {
            expectPackageBlock().getOrCreateSpecTypePair(type).getOrCreateTypeBlock(configuration).apply {
                map.forEach { (name, value) -> getOrCreateEntry(name).setTo(value) }
            }
        }

        /**
         * Open a resource file, creating it if the file does not exist.
         *
         * @param path The resource file path.
         * @return The corresponding [ResourceFile],
         */
        fun openFile(path: String) = ResourceFile(
            resources.createHandle(path), archive, this
        )

        /**
         * Open a [DomFileEditor] for a resource file in the archive.
         *
         * @see openFile
         * @param path The resource file path.
         * @return A [DomFileEditor].
         */
        fun openXmlFile(path: String) = DomFileEditor(openFile(path))
    }

    val resources = ResourceContainer(module.tableBlock)

    internal inner class BytecodeData {
        private val dexFile = MultiDexContainerBackedDexFile(object : MultiDexContainer<DexBackedDexFile> {
            // Load all dex files from the apk module and create a dex entry for each of them.
            private val entries = module.listDexFiles().map {
                BasicDexEntry(
                    this,
                    it.name,
                    it.openStream().use { stream -> RawDexIO.readRawDexFile(stream, it.length, null) })
            }.associateBy { it.entryName }

            override fun getDexEntryNames() = entries.keys.toList()
            override fun getEntry(entryName: String) = entries[entryName]
        })
        private val opcodes = dexFile.opcodes

        /**
         * The classes and proxied classes of the [Base] apk file.
         */
        val classes = ProxyBackedClassList(dexFile.classes)

        /**
         * Write [classes] to the archive.
         *
         */
        internal fun writeDexFiles() {
            // Aplicar proxies a todas las clases
            val classes = classes.also(ProxyBackedClassList::applyProxies)
            val opcodes = opcodes

            // Crear el nuevo DexFile con las clases modificadas
            val newDexFile = object : DexFile {
                    override fun getClasses() = classes.toSet()
                    override fun getOpcodes() = opcodes
                }

            // Crear un directorio temporal único
            val tempDir = File(System.getProperty("java.io.tmpdir"), "extenre_dex_${System.currentTimeMillis()}")
            tempDir.mkdirs()

            try {
                // Escribir los archivos DEX en el directorio temporal
                MultiDexIO.writeDexFile(
                    true,                       // allowSuboptimalApi
                    -1,                         // coreCount (auto)
                    tempDir,                   // Directorio de salida
                    Patcher.dexFileNamer,       // Namer para los nombres de los DEX
                    newDexFile,                // DexFile modificado
                    DexIO.DEFAULT_MAX_DEX_POOL_SIZE,
            null                       // errorHandler
                )

                // Leer todos los archivos .dex generados y agregarlos al APK
                tempDir.listFiles { file -> file.extension == "dex" }?.forEach { dexFile ->
                module.apkArchive.add(object : InputSource(dexFile.name) {
                override fun getMethod() = ZipEntry.DEFLATED
                override fun getLength(): Long = dexFile.length()
                override fun openStream() = dexFile.inputStream()
            })
        }
    } finally {
        // Limpiar: eliminar el directorio temporal y su contenido
        tempDir.deleteRecursively()
        }
    }
} // ← ¡CIERRE DE BYTECODEDATA!

    /**
     * Metadata about an [Apk] file.
     *
     * @param packageName The package name of the [Apk] file.
     * @param packageVersion The package version of the [Apk] file.
     */
    data class PackageMetadata(val packageName: String, val packageVersion: String?) {
        internal constructor(manifestBlock: AndroidManifestBlock) : this(
            manifestBlock.packageName ?: "unnamed split apk file", manifestBlock.versionName
        )
    }

    /**
     * An [Apk] of type [Split].
     *
     * @param config The device configuration associated with this [Split], such as arm64_v8a, en or xhdpi.
     * @see Apk
     */
    sealed class Split(val config: String, module: ApkModule) : Apk(module) {
        override fun toString() = "split_config.$config.apk"

        /**
         * The split apk file which contains libraries.
         *
         * @see Split
         */
        class Library internal constructor(config: String, module: ApkModule) : Split(config, module) {
            companion object {
                /**
                 * A set of all architectures supported by android.
                 */
                val architectures = setOf("armeabi_v7a", "arm64_v8a", "x86", "x86_64")
            }

            // Library splits do not have a resource table.
            override fun updatePackageBlock() {}
        }

        /**
         * The split apk file which contains language strings.
         *
         * @see Split
         */
        class Language internal constructor(config: String, module: ApkModule) : Split(config, module)

        /**
         * The split apk file which contains assets.
         *
         * @see Split
         */
        class Asset internal constructor(config: String, module: ApkModule) : Split(config, module)
    }

    /**
     * The base apk file that is to be patched.
     *
     * @see Apk
     */
    class Base internal constructor(module: ApkModule) : Apk(module) {
        /**
         * Data of the [Base] apk file.
         */
        internal val bytecodeData = BytecodeData()

        override fun toString() = "base.apk"

        override fun write(options: PatcherOptions, file: File) {
            options.logger.info("Writing patched dex files")
            bytecodeData.writeDexFiles()

            super.write(options, file)
        }
    }

    /**
     * An exception thrown when working with [Apk]s.
     *
     * @param message The exception message.
     * @param throwable The corresponding [Throwable].
     */
    sealed class ApkException(message: String, throwable: Throwable? = null) : Exception(message, throwable) {
        /**
         * An exception when decoding resources.
         *
         * @param message The exception message.
         * @param throwable The corresponding [Throwable].
         */
        class Decode(message: String, throwable: Throwable? = null) : ApkException(message, throwable)

        /**
         * An exception when encoding resources.
         *
         * @param message The exception message.
         * @param throwable The corresponding [Throwable].
         */
        class Encode(message: String, throwable: Throwable? = null) : ApkException(message, throwable)

        /**
         * An exception thrown when a reference could not be resolved.
         *
         * @param ref The invalid reference.
         * @param throwable The corresponding [Throwable].
         */
        class InvalidReference(ref: String, throwable: Throwable? = null) :
            ApkException("Failed to resolve: $ref", throwable) {
            constructor(type: String, name: String, throwable: Throwable? = null) : this("@$type/$name", throwable)
        }

        /**
         * An exception thrown when the [Apk] does not have a resource table, but was expected to have one.
         */
        object MissingResourceTable : ApkException("Apk does not have a resource table.")
    }
}
