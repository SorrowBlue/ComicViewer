plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf.edit"
//        resourcePrefix = "bookshelf_edit"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.soil.form)
                implementation(libs.filekit.compose)
            }
        }
    }
}
