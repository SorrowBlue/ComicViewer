plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.file.nav"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.domain.model)
            api(libs.androidx.navigation3Runtime)
        }
    }
}
