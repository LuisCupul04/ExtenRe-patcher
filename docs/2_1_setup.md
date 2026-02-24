<p align="center">
  <picture>
    <source
      width="350px"
      media="(prefers-color-scheme: dark)"
      srcset="Logo/ExtenRe-headline/ExtenRe-Red.svg">
    <img src="Logo/ExtenRe-headline/ExtenRe-Red.svg" alt="ExtenRe Patcher Logo" width="350px">
  </picture>
</p>

# 👨‍💻 Setting up a development environment

To start developing patches with ExtenRe Patcher, you must prepare a development environment.

## 📝 Prerequisites

- A Java IDE with Kotlin support, such as [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- Knowledge of Java, [Kotlin](https://kotlinlang.org), and [Dalvik bytecode](https://source.android.com/docs/core/runtime/dalvik-bytecode)
- Android reverse engineering skills and tools such as [jadx](https://github.com/skylot/jadx)

## 🏃 Prepare the environment

Throughout the documentation, [ExtenRe Patches](https://github.com/LuisCupul04/ExtenRe-patches) will be used as an example project.

1. Clone the repository

   ```bash
   git clone https://github.com/LuisCupul04/ExtenRe-patches && cd ExtenRe-patches
   ```

2. Build the project

   ```bash
   ./gradlew build
   ```

> [!NOTE]
> If the build fails due to authentication, you may need to authenticate to GitHub Packages.
> Create a PAT with the scope `read:packages` [here](https://github.com/settings/tokens/new?scopes=read:packages&description=ExtenRe) and add your token to ~/.gradle/gradle.properties.
>
> Example `gradle.properties` file:
>
> ```properties
> gpr.user = user
> gpr.key = key
> ```

3. Open the project in your IDE

> [!TIP]
> It is a good idea to set up a complete development environment for ExtenRe, so that you can also test your patches
> by following the [ExtenRe documentation](https://github.com/LuisCupul04/ExtenRe-documentation).

## ⏭️ What's next

The next page will go into details about a ExtenRe patch.

Continue: [🧩 Anatomy of a patch](2_2_patch_anatomy.md)
