plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
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

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.security"
    resourcePrefix("settings_security")
}
