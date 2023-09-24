plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.display"
    resourcePrefix("settings_display")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)

    implementation(libs.androidx.startup.runtime)
}
