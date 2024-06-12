plugins {
    alias(libs.plugins.comicviewer.android.featureDynamicFeature)
    alias(libs.plugins.comicviewer.koin)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.onedrive"
    resourcePrefix("onedrive")
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.domain.model)
    implementation(projects.feature.library)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.microsoft.graph)
    implementation(libs.microsoft.identity.client.msal)
    implementation(libs.kotlinx.coroutines.jdk8)
}
