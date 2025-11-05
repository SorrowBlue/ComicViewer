plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
}

kotlin {
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

android {
    namespace = "com.sorrowblue.comicviewer.data.reader.zip"
    packaging {
        jniLibs.useLegacyPackaging = false
    }
}
