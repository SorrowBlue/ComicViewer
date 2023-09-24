plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.authentication"
    resourcePrefix("authentication")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)

    implementation(libs.androidx.biometric)
}
