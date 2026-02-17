plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.storage.device"
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
                implementation(libs.androidx.coreKtx)
                implementation(libs.androidx.documentfile)
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
