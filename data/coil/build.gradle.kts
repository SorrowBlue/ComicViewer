plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.coil"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.domain.service)
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
