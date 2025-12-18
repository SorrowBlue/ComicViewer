plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.collection"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.book.nav)
                implementation(projects.feature.collection.add)
                implementation(projects.feature.collection.editor)
                implementation(projects.feature.collection.nav)
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
                implementation(projects.feature.settings.nav)
                implementation(libs.soil.form)
            }
        }
    }
}
