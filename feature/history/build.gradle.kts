plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.history"
//        resourcePrefix("history")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
            }
        }
    }
}
