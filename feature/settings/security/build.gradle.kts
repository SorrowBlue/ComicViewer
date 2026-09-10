plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.settings.security"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
                implementation(projects.feature.authentication.nav)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.biometric)
            }
        }
    }
}
