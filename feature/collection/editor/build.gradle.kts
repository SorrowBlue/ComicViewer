plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.collection.editor"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.collection.nav)
                implementation(libs.soil.form)
            }
        }
    }
}
