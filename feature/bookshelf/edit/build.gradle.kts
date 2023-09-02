plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.edit"
    resourcePrefix("bookshelf_edit")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
}
