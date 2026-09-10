plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.folder"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.book.nav)
                implementation(projects.feature.collection.nav)
                implementation(projects.feature.folder.nav)
                implementation(projects.feature.search.nav)
                implementation(projects.feature.settings.nav)
                implementation(projects.framework.permission)
                implementation(projects.framework.ui.file)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
    }
}
