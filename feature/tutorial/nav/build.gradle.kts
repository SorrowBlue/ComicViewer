plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.tutorial.nav"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.navigation3Runtime)
        }
    }
}
