plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.folder.nav"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
            implementation(libs.androidx.navigation3Runtime)
        }
    }
}
