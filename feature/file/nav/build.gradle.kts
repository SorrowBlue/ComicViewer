plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.comicviewer.navigationLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.file.nav"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.feature.file)
            implementation(projects.domain.model)
            implementation(projects.framework.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3AdaptiveNavigation3)
            implementation(libs.androidx.navigation3UI)
        }
    }
}
