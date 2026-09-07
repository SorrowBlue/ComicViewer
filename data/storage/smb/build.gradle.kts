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
            dependencies {
                runtimeOnly(libs.slf4j.android)
            }
        }
        getByName("androidHostTest") {
            dependsOn(androidJvmTest)
            dependencies {
                runtimeOnly(libs.robolectric)
            }
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
    buildConfigField("SMB_HOST", project.findProperty("smbHost")?.toString().orEmpty())
    buildConfigField("SMB_PORT", project.findProperty("smbPort")?.toString()?.toIntOrNull() ?: 445)
    buildConfigField("SMB_USERNAME", project.findProperty("smbUsername")?.toString().orEmpty())
    buildConfigField("SMB_DOMAIN", project.findProperty("smbDomain")?.toString().orEmpty())
    buildConfigField("SMB_PASSWORD", project.findProperty("smbPassword")?.toString().orEmpty())
    buildConfigField("SMB_PATH", project.findProperty("smbPath")?.toString().orEmpty())
}
