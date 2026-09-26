plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.authentication"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.authentication.nav)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutinesTest)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.biometric)
                implementation(libs.compose.animationGraphics)
            }
        }
    }
}
