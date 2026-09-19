plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.ui.preview"
        androidResources.enable = true
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)
                implementation(projects.framework.ui)
                implementation(projects.domain.model)
                implementation(libs.androidx.navigation3UI)
                implementation(libs.androidx.pagingCommon)
                implementation(libs.coil3.compose)
                implementation(libs.compose.runtime)
                implementation(libs.compose.preview)
                implementation(libs.compose.material3)
                implementation(libs.compose.material3AdaptiveNavigationSuite)
                implementation(libs.compose.edgeToEdgePreview)
            }
        }
        androidMain {
            dependencies {
            }
        }
        jvmMain.dependencies {
        }
    }
}
