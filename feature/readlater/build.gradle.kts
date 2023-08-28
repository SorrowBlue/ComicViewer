plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.readlater"
    resourcePrefix("readlater")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
    implementation(projects.file)
    implementation(projects.folder)
}
