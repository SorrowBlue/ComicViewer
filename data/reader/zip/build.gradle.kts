plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
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
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.startup.runtime)
                implementation(libs.com.sorrowblue.sevenzipjbinding)
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
