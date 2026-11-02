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
                implementation(projects.feature.bookshelf.nav)
                implementation(projects.feature.folder.nav)
                implementation(projects.feature.settings.nav)
                implementation(libs.androidx.pagingCommon)
                implementation(libs.androidx.pagingCompose)
                implementation(libs.coil3.compose)
            }
        }
    }
}
