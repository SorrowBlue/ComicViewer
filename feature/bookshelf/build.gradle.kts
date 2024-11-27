plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf"
    resourcePrefix("bookshelf")
}

dependencies {
    implementation(projects.feature.bookshelf.edit)
    implementation(projects.feature.bookshelf.info)
    implementation(projects.feature.bookshelf.selection)
    implementation(projects.feature.file)
    implementation(projects.feature.folder)
}
