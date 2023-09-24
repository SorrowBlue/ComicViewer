plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.library.compose")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.designsystem"
}

dependencies {
    api(libs.androidx.compose.material)
    api(libs.androidx.compose.material.icons.extended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.window)

    // Android Studio Preview support
    api(libs.androidx.compose.ui.tooling.preview)
    debugApi(libs.androidx.compose.ui.tooling)

    // UI Tests
    debugApi(libs.androidx.compose.ui.test.manifest)
}
