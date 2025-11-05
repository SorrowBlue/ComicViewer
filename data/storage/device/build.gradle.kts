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
                implementation(libs.filekit.compose)
                implementation(libs.squareup.okio)
                implementation(libs.compose.multiplatform.coreUri)
            }
        }
        androidMain {
            dependencies {
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

android {
    namespace = "com.sorrowblue.comicviewer.data.storage.device"
}
