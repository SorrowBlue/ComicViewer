plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.selection"
    resourcePrefix("bookshelf_selection")
}
