plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.folder"
//        resourcePrefix("folder")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.file)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
    }
}
