plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.authentication"
    resourcePrefix("authentication")
}

dependencies {
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.compose.animation.graphics)
}
