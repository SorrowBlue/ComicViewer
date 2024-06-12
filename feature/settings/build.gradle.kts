plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings"
    resourcePrefix("settings")
}

dependencies {
    implementation(projects.feature.settings.common)
    implementation(projects.feature.settings.display)
    implementation(projects.feature.settings.folder)
    implementation(projects.feature.settings.info)
    implementation(projects.feature.settings.security)
    implementation(projects.feature.settings.viewer)
    implementation(projects.feature.authentication)

    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.billingclient.billingKtx)
}
