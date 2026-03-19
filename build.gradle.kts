import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
    signing
}

group = "com.extenre"

tasks {
    processResources {
        expand("projectVersion" to project.version)
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    maven {
        url = uri("https://maven.pkg.github.com/luiscupul04/smali-RE")
        credentials {
            username = project.findProperty("gpr.user") as? String ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as? String ?: System.getenv("GITHUB_TOKEN")
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/luiscupul04/multidexlib2")
        credentials {
            username = project.findProperty("gpr.user") as? String ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as? String ?: System.getenv("GITHUB_TOKEN")
        }
    }
    // Si también usas Apktool-Re desde GitHub Packages, añade:
    // maven {
    //     url = uri("https://maven.pkg.github.com/luiscupul04/Apktool")
    //     credentials { username = githubUsername; password = githubPassword }
    // }
}

dependencies {
    // TODO: Convert project to KMP.
    compileOnly(libs.android) {
        // Exclude, otherwise the org.w3c.dom API breaks.
        exclude(group = "xerces", module = "xmlParserAPIs")
    }

    implementation(libs.apktool.lib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.multidexlib2)
    implementation(libs.smali)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)

        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

tasks.withType<Test> {
    testLogging {
        // Uncomment to show println and exception stack traces in unit tests.
        // showStandardStreams = true
    }
}


java {
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/LuisCupul04/extenre-patcher")
            credentials {
                username = providers.gradleProperty("gpr.user").getOrElse(System.getenv("GITHUB_ACTOR"))
                password = providers.gradleProperty("gpr.key").getOrElse(System.getenv("GITHUB_TOKEN"))
            }
        }
    }

    publications {
        create<MavenPublication>("extenre-patcher-publication") {
            from(components["java"])

            version = project.version.toString()

            pom {
                name = "ExtenRe Patcher"
                description = "ExtenRe Project."

                licenses {
                    license {
                        name = "GNU General Public License v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                    }
                }
                developers {
                    developer {
                        id = "ExtenRe"
                        name = "LuisCupul04"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/luiscupul04/extenre-patcher.git"
                    developerConnection = "scm:git:git@github.com:luiscupul04/extenre-patcher.git"
                    url = "https://github.com/luiscupul04/extenre-patcher"
                }
            }
        }
    }
}

signing {
    isRequired = false
    sign(publishing.publications["extenre-patcher-publication"])
}
