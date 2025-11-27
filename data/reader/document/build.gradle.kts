plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.di)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.reader.document"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.storage.client)
            }
        }
        androidMain {
            dependencies {
                implementation(projects.data.reader.document.android)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.startupRuntime)
            }
        }
        desktopMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}
