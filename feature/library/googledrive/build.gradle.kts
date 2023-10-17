plugins {
    id("comicviewer.android.feature.dynamic-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.googledrive"
    resourcePrefix("googledrive")
    packaging {
        resources.excludes += "META-INF/DEPENDENCIES"
    }
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.domain.model)
    implementation(projects.feature.library)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.google.android.gms.play.services.auth)
    implementation(libs.google.api.client.android)
    implementation(libs.google.api.services.drive)
    implementation(libs.kotlinx.coroutines.play.services)
}
