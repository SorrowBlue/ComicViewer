plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.reader.zip"
        packaging {
            jniLibs.useLegacyPackaging = false
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.storage.client)
                implementation(libs.kotlinx.coroutinesCore)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.startupRuntime)
                implementation(libs.sevenzipjbinding.android)
            }
        }
        desktopMain {
            dependencies {
                implementation(libs.sevenzipjbinding)
                implementation(libs.sevenzipjbinding.allPlatforms)
            }
        }
    }
}
