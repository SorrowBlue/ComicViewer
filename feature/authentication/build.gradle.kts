plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.authentication"
        androidResources.enable = true
    }
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.biometric)
                implementation(libs.compose.animationGraphics)
            }
        }
    }
}
