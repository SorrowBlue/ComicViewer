plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.library.compose")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.designsystem"
}

dependencies {
    api(libs.google.material)
    api(libs.androidx.window)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)

    // Android Studio Preview support
    api(libs.androidx.compose.ui.toolingPreview)
    debugApi(libs.androidx.compose.ui.tooling)

    // UI Tests
    debugApi(libs.androidx.compose.ui.testManifest)
}
