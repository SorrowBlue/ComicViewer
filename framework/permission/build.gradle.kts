plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
}

kotlin {
    android {
        androidResources.enable = true
        namespace = "com.sorrowblue.comicviewer.framework.permission"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)
                implementation(projects.framework.ui)
                implementation(libs.compose.componentsResources)
                implementation(libs.compose.material3)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.activityCompose)
            }
        }
    }
}
