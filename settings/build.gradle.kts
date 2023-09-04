plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.settings"
    resourcePrefix("settings")
    defaultConfig {
        buildConfigField("long", "TIMESTAMP", "${System.currentTimeMillis()}L")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.framework.settings)
    implementation(projects.domain)
    implementation(projects.settings.display)
    implementation(projects.settings.viewer)
    implementation(projects.settings.folder)
    implementation(projects.settings.security)
}
