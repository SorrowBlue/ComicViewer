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
                implementation(projects.framework.ui.file)
                implementation(projects.feature.bookshelf.nav)
                implementation(projects.feature.folder.nav)
                implementation(projects.feature.settings.nav)
            }
        }
    }
}
