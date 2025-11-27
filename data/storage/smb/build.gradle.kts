plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.di)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.smb"
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
        androidMain {
            dependencies {
                implementation(libs.jcifs)
                implementation(libs.slf4j.android)
                implementation(libs.androidx.documentfile)
            }
        }
        desktopMain {
            dependencies {
                implementation(libs.jcifs)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.kmpfile.filekit)
                implementation(libs.kmpfile.okio)
            }
        }
    }
}
