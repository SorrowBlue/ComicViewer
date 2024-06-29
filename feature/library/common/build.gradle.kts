plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.common"
    resourcePrefix("library_common")
}

dependencies {
    implementation(projects.framework.notification)
    implementation(libs.androidx.documentfile)
}
