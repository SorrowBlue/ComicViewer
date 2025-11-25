plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.settings.security"
        // resourcePrefix("settings_security")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.biometric)
            }
        }
    }
}
