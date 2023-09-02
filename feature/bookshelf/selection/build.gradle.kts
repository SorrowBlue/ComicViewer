plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.selection"
    resourcePrefix("bookshelf_selection")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
}
