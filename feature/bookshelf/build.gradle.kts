plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.book.nav)
                implementation(projects.feature.bookshelf.nav)
                implementation(projects.feature.collection.nav)
                implementation(projects.framework.ui.file)
                implementation(projects.feature.folder)
                implementation(projects.feature.folder.nav)
                implementation(projects.feature.search.nav)
                implementation(projects.feature.settings.nav)
            }
        }
    }
}
