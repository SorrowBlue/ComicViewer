plugins {
    id("build-logic.android.library")
}

android {
    viewBinding.enable = true
}

dependencies {
    implementation(libs.androidx.preference.ktx)
    implementation(libs.google.material)
}
