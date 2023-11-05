plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.security"
}

dependencies {
    implementation(projects.feature.settings.common)

    implementation(libs.androidx.biometric)
}
