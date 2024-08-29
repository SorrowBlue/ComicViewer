plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.edit"
    resourcePrefix("bookshelf_edit")
}

dependencies {
    implementation(libs.soil.form)
    implementation("com.google.android.material:material:1.13.0-alpha05")
}
