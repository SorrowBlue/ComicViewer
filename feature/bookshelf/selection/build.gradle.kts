plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf.selection"
//        resourcePrefix("bookshelf_selection")
    }
}

