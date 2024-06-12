plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.security"
    resourcePrefix("settings_security")
}

dependencies {
    implementation(projects.feature.settings.common)

    implementation(libs.androidx.biometric)
}
