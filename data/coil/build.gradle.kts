plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.data.coil"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.domain.service)
                implementation(projects.data.storage)
                implementation(libs.coil3)
                implementation(libs.kotlinx.serializationJson)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.startupRuntime)
            }
        }
    }
}
