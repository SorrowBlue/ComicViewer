plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.file"
    resourcePrefix("file")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
}
