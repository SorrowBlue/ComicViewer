plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.settings.common"
    }
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
        )
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
                api(libs.compose.material3Adaptive)
                implementation(libs.compose.material3AdaptiveLayout)
                implementation(libs.compose.preview)
            }
        }
    }
}
