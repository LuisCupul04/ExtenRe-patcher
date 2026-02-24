<p align="center">
  <picture>
    <source
      width="350px"
      media="(prefers-color-scheme: dark)"
      srcset="Logo/ExtenRe-headline/ExtenRe-Red.svg">
    <img src="Logo/ExtenRe-headline/ExtenRe-Red.svg" alt="ExtenRe Patcher Logo" width="350px">
  </picture>
</p>

# 💉 Introduction to ExtenRe Patcher

To create patches for Android apps, it is recommended to know the basic concept of ExtenRe Patcher.

## 📙 How it works

ExtenRe Patcher is a library that allows modifying Android apps by applying patches.
It is built on top of [Smali](https://github.com/google/smali) for bytecode manipulation and [Androlib (Apktool)](https://github.com/iBotPeaches/Apktool)
for resource decoding and encoding.

ExtenRe Patcher receives a list of patches and applies them to a given APK file.
It then returns the modified components of the APK file, such as modified dex files and resources,
that can be repackaged into a new APK file.

ExtenRe Patcher has a simple API that allows you to load patches from RVP (JAR or DEX container) files
and apply them to an APK file. Later on, you will learn how to create patches.

```kt
val patches = loadPatchesFromJar(setOf(File("ExtenRe-patches.rvp")))

val patcherResult = Patcher(PatcherConfig(apkFile = File("some.apk"))).use { patcher ->
    // Here you can access metadata about the APK file through patcher.context.packageMetadata
    // such as package name, version code, version name, etc.

    // Add patches.
    patcher += patches

    // Execute the patches.
    runBlocking {
        patcher().collect { patchResult ->
            if (patchResult.exception != null)
                logger.info { "\"${patchResult.patch}\" failed:\n${patchResult.exception}" }
            else
                logger.info { "\"${patchResult.patch}\" succeeded" }
        }
    }

    // Compile and save the patched APK file components.
    patcher.get()
}

// The result of the patcher contains the modified components of the APK file that can be repackaged into a new APK file.
val dexFiles = patcherResult.dexFiles
val resources = patcherResult.resources
```

## ⏭️ What's next

The next page teaches the fundamentals of ExtenRe Patches.

Continue: [🧩 Introduction to ExtenRe Patches](2_patches_intro.md)
