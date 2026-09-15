plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.navigationLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.folder.nav"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
        }
    }
}
