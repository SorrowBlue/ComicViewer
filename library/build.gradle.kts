plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.library"
    resourcePrefix("library")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
    implementation(projects.feature.history)
}
