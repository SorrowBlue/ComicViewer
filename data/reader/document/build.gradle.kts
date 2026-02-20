plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
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
                implementation(libs.kotlinx.coroutinesCore)
                implementation(libs.androidx.coreKtx)
                implementation(libs.androidx.startupRuntime)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.kotlinx.coroutinesCore)
            }
        }
    }
}
