plugins {
    id("comicviewer.android.feature")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.edit"
    resourcePrefix("bookshelf_edit")
}

dependencies {
    implementation(libs.androidx.documentfile)
}
