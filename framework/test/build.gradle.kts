plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.compose)
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.test"
}

dependencies {
    debugImplementation(libs.androidx.compose.ui.testManifest)
}
