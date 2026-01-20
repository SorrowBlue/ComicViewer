plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.settings.display"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
            }
        }
    }
}
