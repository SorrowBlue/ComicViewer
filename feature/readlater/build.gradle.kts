plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.readlater"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.book.nav)
                implementation(projects.feature.collection.nav)
                implementation(projects.feature.file.nav)
                implementation(projects.feature.folder.nav)
                implementation(projects.feature.settings.nav)
            }
        }
    }
}
