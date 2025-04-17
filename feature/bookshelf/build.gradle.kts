plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.bookshelf.info)
                implementation(projects.feature.bookshelf.edit)
                implementation(projects.feature.bookshelf.selection)
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf"
    resourcePrefix("bookshelf")
}
