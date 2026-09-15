plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.framework.navigation"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.domain.model)
                implementation(libs.compose.runtime)
                implementation(libs.compose.ui)
                implementation(libs.androidx.navigation3Runtime)
            }
        }
    }
}
