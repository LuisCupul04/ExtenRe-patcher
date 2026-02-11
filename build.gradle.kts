plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
    signing
}

group = "com.extenre"

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
    implementation(libs.smali)
    
    implementation(libs.multidexlib2) {
        exclude(group = "com.android.tools.smali", module = "smali-dexlib2")
    }
    implementation(libs.smali.dexlib2) // ← tu fork, versión 3.0.9.RE

    implementation(libs.guava)
    
    implementation(libs.arscLib)
    implementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test)
    compileOnly(libs.android)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }
    processResources {
        expand("projectVersion" to project.version)
    }
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/luiscupul04/extenre-patcher")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("extenre-patcher-publication") {
            from(project.components["java"])

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
                        id = "LuisCupul04"
                        name = "LuisCupul04"
                    }
                    developer {
                        id = "ReVanced"
                        name = "ReVanced"
                        email = "contact@revanced.app"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/LuisCupul04/extenre-patcher.git"
                    developerConnection = "scm:git:git@github.com:LuisCupul04/extenre-patcher.git"
                    url = "https://github.com/LuisCupul04/extenre-patcher"
                } // ← CIERRE DE SCM
            } // ← CIERRE DE POM
        } // ← CIERRE DE CREATE
    } // ← CIERRE DE PUBLICATIONS
} // ← CIERRE DE PUBLISHING

signing {
    useGpgCmd()
    sign(publishing.publications["extenre-patcher-publication"])
} // ← CIERRE DE SIGNING
