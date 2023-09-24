plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.security"
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)

    implementation(libs.androidx.biometric)
}
