plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.folder"
    resourcePrefix("settings_folder")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)

    implementation(libs.androidx.startup.runtime)
}
