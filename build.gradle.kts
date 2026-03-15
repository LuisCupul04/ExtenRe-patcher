plugins {
    kotlin("jvm") version "2.0.0"
    `java-library`      // Añadido para usar variantes modernas de atributos
    `maven-publish`
    signing
}

group = "com.extenre"

// Credenciales para GitHub Packages
val githubUsername: String = project.findProperty("gpr.user") as? String ?: System.getenv("GITHUB_ACTOR")
val githubPassword: String = project.findProperty("gpr.key") as? String ?: System.getenv("GITHUB_TOKEN")

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://maven.google.com") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    maven {
        url = uri("https://maven.pkg.github.com/luiscupul04/smali-RE")
        credentials {
            username = githubUsername
            password = githubPassword
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/luiscupul04/multidexlib2")
        credentials {
            username = githubUsername
            password = githubPassword
        }
    }
}

dependencies {
    implementation(libs.xpp3)
    implementation(libs.smali) //com.extenre:smali
    implementation(libs.multidexlib2)   // Trae smali-dexlib2 transitivamente
    implementation(libs.reandroid.arsc)
    implementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test)
    compileOnly(libs.google.android)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging { events("PASSED", "SKIPPED", "FAILED") }
    }
    processResources {
        expand("projectVersion" to project.version)
    }
}

java {
    withSourcesJar()   // Esto ya añade el jar de fuentes al componente 'java'
}

kotlin {
    jvmToolchain(21)
}

publishing {
    repositories {
        // Repositorio para GitHub Packages (siempre agregado)
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/luiscupul04/ExtenRe-patcher")
            credentials {
                username = githubUsername
                password = githubPassword
            }
        }

        // Repositorio para OSSRH (Sonatype) - solo si existen credenciales
        val ossrhToken = System.getenv("OSSRH_TOKEN")
        val ossrhPassword = System.getenv("OSSRH_PASSWORD")
        if (ossrhToken != null && ossrhPassword != null) {
            maven {
                name = "OSSRH"
                url = if (version.toString().endsWith("SNAPSHOT"))
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                else
                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = ossrhToken
                    password = ossrhPassword
                }
            }
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            // Forzar el uso de atributos modernos (java-api y java-runtime en lugar de java-api-jars)
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            // Suprimir advertencias de metadatos duplicados (opcional)
            suppressAllPomMetadataWarnings()

            // No es necesario agregar el sources jar explícitamente porque 'withSourcesJar()' ya lo incluye
            // artifact(tasks["sourcesJar"])  // ← Línea eliminada para evitar duplicado

            // Coordenadas del artefacto (opcional, por defecto toma las del proyecto)
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}

signing {
    if (System.getenv("GPG_KEY_ID") == null ||
        System.getenv("GPG_KEY") == null ||
        System.getenv("GPG_KEY_PASSWORD") == null) return@signing
    useInMemoryPgpKeys(
        System.getenv("GPG_KEY_ID"),
        System.getenv("GPG_KEY"),
        System.getenv("GPG_KEY_PASSWORD"),
    )
    sign(publishing.publications)
}