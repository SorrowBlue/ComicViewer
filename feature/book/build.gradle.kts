plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.book"
    resourcePrefix("book")
}
