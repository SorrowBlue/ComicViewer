plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.authentication"
    resourcePrefix("authentication")
}

dependencies {
    implementation(libs.androidx.biometric)
}
