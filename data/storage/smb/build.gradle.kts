plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.buildconfig)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.data.smb"
        withHostTest {}
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.storage)
                implementation(libs.kotlinx.coroutinesCore)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutinesTest)
            }
        }
        val androidJvm = create("androidJvm") {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.jcifs)
            }
        }
        val androidJvmTest = create("androidJvmTest") {
            dependsOn(commonTest.get())
        }
        androidMain {
            dependsOn(androidJvm)
        }
        named("androidHostTest") {
            dependsOn(androidJvmTest)
        }
        jvmMain {
            dependsOn(androidJvm)
        }
        jvmTest {
            dependsOn(androidJvmTest)
        }
    }
}

buildConfig {
    packageName = "com.sorrowblue.comicviewer.data.smb"
    buildConfigField("SMB_HOST", providers.gradleProperty("smbHost").orElse(""))
    buildConfigField(
        "SMB_PORT",
        providers.gradleProperty("smbPort").map { it.toIntOrNull() ?: 445 }.orElse(445),
    )
    buildConfigField("SMB_USERNAME", providers.gradleProperty("smbUsername").orElse(""))
    buildConfigField("SMB_DOMAIN", providers.gradleProperty("smbDomain").orElse(""))
    buildConfigField("SMB_PASSWORD", providers.gradleProperty("smbPassword").orElse(""))
    buildConfigField("SMB_PATH", providers.gradleProperty("smbPath").orElse(""))
}
