plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.google.android.play.feature.delivery.ktx)
            }
        }

        desktopMain {
            dependencies {
                implementation(libs.filekit.compose)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.tutorial"
    resourcePrefix("tutorial")
}
