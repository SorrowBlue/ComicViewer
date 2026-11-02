plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.navigationLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.search.nav"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.domain.model)
        }
    }
}
