plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf"
        // resourcePrefix("bookshelf")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.bookshelf.edit)
                implementation(projects.feature.bookshelf.info)
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
    }
}
