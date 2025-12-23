plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.book.nav)
                implementation(projects.feature.bookshelf.edit)
                implementation(projects.feature.bookshelf.info)
                implementation(projects.feature.collection.add)
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
                implementation(projects.feature.search)
                implementation(projects.feature.settings.nav)
            }
        }
    }
}
