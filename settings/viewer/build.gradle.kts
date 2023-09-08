plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.settings.viewer"
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
}
