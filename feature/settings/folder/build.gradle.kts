plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.settings.folder"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
            }
        }
    }
}
