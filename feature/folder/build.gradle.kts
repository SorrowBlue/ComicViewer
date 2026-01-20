plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.folder"
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
