plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.folder"
    resourcePrefix("settings_folder")
}

dependencies {
    implementation(projects.framework.common)
    implementation(projects.feature.settings.common)

    implementation(libs.androidx.startup.runtime)
    implementation(libs.google.android.play.feature.delivery.ktx)
}
