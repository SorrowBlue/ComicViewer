plugins {
    id("build-logic.android.library")
}
dependencies {
    api(libs.squareup.logcat)
    implementation(libs.androidx.startup.runtime)
}
