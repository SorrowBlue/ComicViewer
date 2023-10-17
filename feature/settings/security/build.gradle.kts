plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.security"
}

dependencies {
    implementation(libs.androidx.biometric)
}
