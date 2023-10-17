plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings"
    resourcePrefix("settings")
    defaultConfig {
        buildConfigField("long", "TIMESTAMP", "${System.currentTimeMillis()}L")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.feature.settings.display)
    implementation(projects.feature.settings.viewer)
    implementation(projects.feature.settings.folder)
    implementation(projects.feature.settings.security)

    implementation(libs.androidx.appcompat)
}
