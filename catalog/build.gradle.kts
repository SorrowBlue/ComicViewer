plugins {
    alias(libs.plugins.comicviewer.android.application)
    alias(libs.plugins.comicviewer.android.compose)
    alias(libs.plugins.semver)
}

android {
    namespace = "com.sorrowblue.comicviewer.catalog"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer.catalog"
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = semver.gitDescribed.toString()
    }
}

dependencies {
    implementation(projects.feature.authentication)
    implementation(projects.feature.file)

    implementation(libs.androidx.appcompat)
    compileOnly(libs.androidx.compose.material3)
    compileOnly(libs.androidx.compose.material3.adaptive.layout)

    implementation(libs.airbnb.android.showkase)
}
