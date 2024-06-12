import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties


plugins {
    alias(libs.plugins.comicviewer.android.featureDynamicFeature)
    alias(libs.plugins.comicviewer.koin)
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.dropbox"
    resourcePrefix("dropbox")

    buildTypes {
        val dropboxApiKey = System.getenv("DROP_BOX_API_KEY")
            ?: gradleLocalProperties(rootDir, providers).getProperty("DROP_BOX_API_KEY")
        if (dropboxApiKey.isNullOrEmpty()) {
            logger.warn("DROP_BOX_API_KEY is not set.")
        } else {
            all {
                manifestPlaceholders += mapOf("dropbox_api_key" to dropboxApiKey)
            }
        }
    }
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.domain.model)
    implementation(projects.feature.library)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.dropbox.androidSdk)
    implementation(libs.kotlinx.serialization.protobuf)
}
