plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.settings.plugin"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
            }
        }
        desktopMain {
            dependencies {
                implementation(libs.filekit.compose)
            }
        }
    }
}
