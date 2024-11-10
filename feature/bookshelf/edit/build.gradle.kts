plugins {
    alias(libs.plugins.comicviewer.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.edit"
    resourcePrefix("bookshelf_edit")
}

dependencies {
    implementation(libs.soil.form)
    implementation(libs.google.android.material)
}
