plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.tutorial"
        // resourcePrefix("tutorial")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.uiBackhandler)
            }
        }
        desktopMain {
            dependencies {
                implementation(libs.filekit.compose)
            }
        }
    }
}
