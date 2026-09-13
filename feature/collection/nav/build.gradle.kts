plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.collection.nav"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
            implementation(libs.androidx.navigation3Runtime)
        }
    }
}
