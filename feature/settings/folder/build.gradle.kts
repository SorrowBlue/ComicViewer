plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.folder"
    resourcePrefix("settings_folder")
}

dependencies {
    implementation(projects.feature.settings.common)
}
