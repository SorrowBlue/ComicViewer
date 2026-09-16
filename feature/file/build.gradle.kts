plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.file"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
