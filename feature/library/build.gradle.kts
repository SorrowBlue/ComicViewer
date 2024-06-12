plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library"
    resourcePrefix("library")
}

dependencies {
    implementation(projects.feature.history)

    implementation(libs.google.android.play.feature.delivery.ktx)
}
