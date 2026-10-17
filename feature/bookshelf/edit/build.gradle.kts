plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf.edit"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.bookshelf.nav)
                implementation(projects.feature.permission.nav)
                implementation(libs.soil.form)
                implementation(libs.filekit.compose)
            }
        }
    }
}
