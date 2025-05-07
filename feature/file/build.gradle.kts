plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.file"
//        resourcePrefix("file")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
