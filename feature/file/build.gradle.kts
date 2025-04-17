plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.file"
    resourcePrefix("file")
}
