plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf"
    resourcePrefix("bookshelf")
}

dependencies {
    implementation(projects.feature.bookshelf.edit)
    implementation(projects.feature.bookshelf.selection)
    implementation(projects.feature.folder)
}
