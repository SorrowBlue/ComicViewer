plugins {
    alias(libs.plugins.comicviewer.android.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.common"
}

dependencies {
    implementation(libs.androidx.startup.runtime)
    implementation(libs.squareup.logcat)
}
