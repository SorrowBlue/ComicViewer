plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.viewer"
}

dependencies {
    implementation(projects.feature.settings.common)
}
