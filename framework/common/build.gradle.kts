plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.common"
}

dependencies {
    implementation(libs.androidx.startup.runtime)
    implementation(libs.squareup.logcat)
}
