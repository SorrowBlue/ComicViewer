plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.notification"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup.runtime)
}
