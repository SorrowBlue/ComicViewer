plugins {
    id("build-logic.android.library")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework"
}

dependencies {
    api(libs.squareup.logcat)
    implementation(libs.androidx.startup.runtime)
}
