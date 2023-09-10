plugins {
    id("com.sorrowblue.android-dynamic-feature")
}

android {
    resourcePrefix("googledrive")
    namespace = "com.sorrowblue.comicviewer.library.googledrive"
    packaging {
        resources.excludes += "META-INF/DEPENDENCIES"
    }
}

dependencies {
    implementation(projects.app)
    implementation(projects.framework.compose)
    implementation(projects.framework.notification)
    implementation(projects.domain)
    implementation(projects.library)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.google.android.gms.play.services.auth)
    implementation(libs.google.api.client.android)
    implementation(libs.google.api.services.drive)
    implementation(libs.kotlinx.coroutines.play.services)
}
