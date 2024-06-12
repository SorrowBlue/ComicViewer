plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.display"
    resourcePrefix("settings_display")
}

dependencies {
    implementation(projects.framework.common)
    implementation(projects.feature.settings.common)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.startup.runtime)
}
