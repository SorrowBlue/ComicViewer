plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.authentication"
//        resourcePrefix("authentication")
    }
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.biometric)
                implementation(libs.androidx.compose.animation.graphics)
            }
        }
    }
}
