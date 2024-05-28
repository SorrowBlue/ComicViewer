plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.library.compose")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.test"
}

dependencies {
    debugImplementation(libs.androidx.compose.ui.testManifest)
}
