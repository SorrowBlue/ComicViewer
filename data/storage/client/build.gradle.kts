plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.di)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.storage"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.service)
                implementation(libs.kotlinx.coroutinesCore)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.startupRuntime)
            }
        }
    }
}
