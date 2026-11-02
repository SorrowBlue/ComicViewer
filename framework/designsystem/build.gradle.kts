plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.designsystem"
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
                implementation(libs.compose.preview)
                implementation(libs.compose.material3)
                implementation(libs.compose.material3Adaptive)
                implementation(libs.compose.materialIconsExtended)
            }
        }
        noAndroid {
            dependencies {
                implementation(projects.domain.model)
            }
        }
    }
}
