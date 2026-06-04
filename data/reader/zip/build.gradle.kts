plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.data.reader.zip"
        packaging {
            jniLibs.useLegacyPackaging = false
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.storage)
                implementation(libs.kotlinx.coroutinesCore)
                implementation(libs.kioarch)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.startupRuntime)
            }
        }
    }
}
