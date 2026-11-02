plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.data.storage.device"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.storage)
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
                implementation(libs.kmpfile)
            }
        }
    }
}
