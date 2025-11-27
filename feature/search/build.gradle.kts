plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.search"
        // resourcePrefix("search")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
                implementation(libs.soil.form)
            }
        }
    }
}
