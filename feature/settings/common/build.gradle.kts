plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.settings.common"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)
                implementation(projects.framework.navigation)
                implementation(projects.framework.ui)
                implementation(projects.framework.ui.preview)


                implementation(libs.androidx.windowCore)
                implementation(libs.compose.material3)
                implementation(libs.compose.material3Adaptive)
                implementation(libs.compose.material3AdaptiveLayout)
                implementation(libs.compose.ui)
                implementation(libs.compose.preview)
            }
        }
    }
}
