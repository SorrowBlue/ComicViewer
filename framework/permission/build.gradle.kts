plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.permission"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.runtime)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.activityCompose)
                implementation(libs.compose.ui)
            }
        }
    }
}
