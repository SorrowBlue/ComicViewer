plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.jsonOkio)
            }
        }
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
dependencies {
    implementation(libs.androidx.ui.android)
}
