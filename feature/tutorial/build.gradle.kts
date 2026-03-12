plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.tutorial"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.uiBackhandler)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.filekit.compose)
            }
        }
    }
}
