plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.designsystem"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.componentsResources)
                implementation(libs.compose.preview)
                implementation(libs.compose.material3)
                implementation(libs.compose.material3Adaptive)
                implementation(libs.compose.materialIconsExtended)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
            }
        }
        noAndroid {
            dependencies {
                implementation(projects.domain.model)
            }
        }
    }
}
