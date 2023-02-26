plugins {
    id("build-logic.android.dynamic-feature")
}

dependencies {
    implementation(projects.app)
    implementation(libs.squareup.logcat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.artifex.mupdf.fitz)
}