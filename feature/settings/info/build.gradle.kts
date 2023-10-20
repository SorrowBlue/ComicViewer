plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.info"
    resourcePrefix("settings_info")
    defaultConfig {
        buildConfigField("long", "TIMESTAMP", "${System.currentTimeMillis()}L")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.feature.settings.common)

    implementation(libs.google.android.play.review.ktx)
    implementation(libs.mikepenz.aboutlibraries)
}
