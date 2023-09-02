plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.book"
    resourcePrefix("book")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
}
