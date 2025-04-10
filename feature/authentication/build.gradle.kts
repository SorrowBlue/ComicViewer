plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.biometric)
                implementation(libs.androidx.compose.animation.graphics)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.authentication"
    resourcePrefix("authentication")
}
