<p align="center">
  <picture>
    <source
      width="350px"
      media="(prefers-color-scheme: dark)"
      srcset="Logo/ExtenRe-headline/ExtenRe-Red.svg">
    <img src="Logo/ExtenRe-headline/ExtenRe-Red.svg" alt="ExtenRe Patcher Logo" width="350px">
  </picture>
</p>

# 💉 ExtenRe Patcher

![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/LuisCupul04/ExtenRe-patcher/release.yml)
![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)

ExtenRe Patcher used to patch Android applications.

## ❓ About

ExtenRe Patcher is a library that is used to patch Android applications.  
It powers [ExtenRe Manager](https://github.com/LuisCupul04/ExtenRe-manager),
[ExtenRe CLI](https://github.com/LuisCupul04/ExtenRe-cli)
and [ExtenRe Library](https://github.com/LuisCupul04/ExtenRe-library) and a rich set of patches have been developed
using ExtenRe Patcher in the [ExtenRe Patches](https://github.com/LuisCupul04/ExtenRe-patches) repository.

## 💪 Features

Some of the features the ExtenRe Patcher provides are:

- 🔧 **Patch Dalvik VM bytecode**: Disassemble and assemble Dalvik bytecode
- 📦 **Patch APK resources**: Decode and build Android APK resources
- 📂 **Patch arbitrary APK files**: Read and write arbitrary files directly from and to APK files
- 🧩 **Write modular patches**: Extensive API to write modular patches that can patch Dalvik VM bytecode,
APK resources and arbitrary APK files

## 🚀 How to get started

To use ExtenRe Patcher in your project, follow these steps:

1. [Add the repository](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)
to your project
2. Add the dependency to your project:

   ```kt
    dependencies {
        implementation("com.extenre:extenre-patcher:{$version}")
    }
   ```

## 📚 Everything else

### 📙 Contributing

Thank you for considering contributing to ExtenRe Patcher.
You can find the contribution guidelines [here](CONTRIBUTING.md).

### 🛠️ Building

To build ExtenRe Patcher,
you can follow the [ExtenRe documentation](https://github.com/LuisCupul04/ExtenRe-documentation).

### 📃 Documentation

The documentation contains the fundamentals of ExtenRe Patcher and how to use ExtenRe Patcher to create patches.
    You can find it [here](https://github.com/LuisCupul04/ExtenRe-patcher/tree/main/docs).

## 📜 Licence

ExtenRe Patcher is licensed under the GPLv3 license. Please see the [licence file](LICENSE) for more information.
[tl;dr](https://www.tldrlegal.com/license/gnu-general-public-license-v3-gpl-3) you may copy, distribute and modify ExtenRe Patcher as long as you track changes/dates in source files.
Any modifications to ExtenRe Patcher must also be made available under the GPL,
along with build & install instructions.

## 🙏 Acknowledgements

ExtenRe Patcher is a reconstruction and continuation of [ReVanced Extended](https://github.com/inotia00/ReVanced_Extended) (RVX) by [inotia00](https://github.com/inotia00). While the original RVX project is no longer actively maintained, its foundational work and ideas have been instrumental in shaping this project.

It is important to clarify that **ExtenRe is not intended to replace RVX, nor does it aim to compete with ReVanced or inotia00's new project, MorpheApp**. Instead, ExtenRe is a separate project that builds upon the legacy of RVX, introducing new changes and improvements, while also removing or replacing certain features—including code and functions that are no longer useful—to better align with its own vision and goals.

ExtenRe Patcher also builds upon the original [ReVanced Patcher](https://github.com/inotia00/revanced-patcher) by ReVanced LLC, and we thank them for their pioneering work.