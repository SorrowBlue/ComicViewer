plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf.info"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.notification)
                implementation(projects.feature.bookshelf.edit)
                implementation(projects.feature.file)
                implementation(libs.androidx.coreUri)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.workRuntime)
                implementation(libs.metro.android)
            }
        }
    }
}
