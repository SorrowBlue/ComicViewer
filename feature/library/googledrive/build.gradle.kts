plugins {
    alias(libs.plugins.comicviewer.android.featureDynamicFeature)
    alias(libs.plugins.comicviewer.koin)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.googledrive"
    resourcePrefix("googledrive")
    packaging {
        resources.excludes += "META-INF/DEPENDENCIES"
        resources.excludes += "META-INF/INDEX.LIST"
    }
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.domain.model)
    implementation(projects.feature.library)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.playServicesAuth)
    implementation(libs.google.android.gms.play.services.auth)
    implementation("com.google.apis:google-api-services-people:v1-rev20240313-2.0.0")
    implementation(libs.google.api.client.android)
    implementation(libs.google.api.services.drive)
    implementation(libs.kotlinx.coroutines.play.services)
}
