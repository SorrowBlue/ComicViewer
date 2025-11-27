plugins {
    alias(libs.plugins.comicviewer.androidLibrary)
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.notification"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startupRuntime)
}
