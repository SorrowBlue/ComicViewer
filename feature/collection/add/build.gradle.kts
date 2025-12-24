plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.collection.add"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.collection.editor)
            }
        }
    }
}
