plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.settings.security"
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)

    implementation(libs.androidx.biometric)
}
