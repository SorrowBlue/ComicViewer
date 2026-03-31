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
                implementation(projects.data.storage.client)
                implementation(libs.kotlinx.coroutinesCore)
                implementation(libs.filekit.compose)
                implementation(libs.okio)
                implementation(libs.androidx.coreUri)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutinesTest)
            }
        }
        val androidJvm by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.jcifs)
            }
        }
        val androidJvmTest by creating {
            dependsOn(commonTest.get())
        }
        androidMain {
            dependsOn(androidJvm)
            dependencies {
                implementation(libs.jcifs)
                implementation(libs.slf4j.android)
                implementation(libs.androidx.documentfile)
            }
        }
        getByName("androidHostTest") {
            dependsOn(androidJvmTest)
            dependencies {
                implementation(libs.robolectric)
            }
        }
        jvmMain {
            dependsOn(androidJvm)
            dependencies {
                implementation(libs.jcifs)
            }
        }
        jvmTest {
            dependsOn(androidJvmTest)
        }
        iosMain {
            dependencies {
                implementation(libs.kmpfile.filekit)
                implementation(libs.kmpfile.okio)
            }
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
