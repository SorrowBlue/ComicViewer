plugins {
    alias(libs.plugins.comicviewer.android.featureDynamicFeature)
    alias(libs.plugins.comicviewer.koin)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.dropbox"
    resourcePrefix("dropbox")

    buildTypes {
        val comicviewerDropboxApiKey: String? by project
        if (comicviewerDropboxApiKey.isNullOrEmpty()) {
            logger.warn("DROP_BOX_API_KEY is not set.")
        } else {
            all {
                manifestPlaceholders += mapOf("dropbox_api_key" to comicviewerDropboxApiKey!!)
            }
        }
    }
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.feature.library.common)
    implementation(projects.domain.model)
    implementation(projects.feature.library)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.dropbox.androidSdk)
    implementation(libs.kotlinx.serialization.protobuf)
}
