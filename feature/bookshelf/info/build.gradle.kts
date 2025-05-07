plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf.info"
//        resourcePrefix("bookshelf_info")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.file)
                implementation(libs.compose.multiplatform.coreUri)
            }
        }
        androidMain {
            dependencies {
                implementation(projects.framework.notification)

                implementation(libs.androidx.work.runtime)
                implementation(libs.koin.androidxWorkmanager)
            }
        }
    }
}
