import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("comicviewer.android.feature.dynamic-feature")
    id("comicviewer.android.koin")
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.dropbox"
    resourcePrefix("dropbox")

    buildTypes {
        all {
            manifestPlaceholders += mapOf(
                "dropbox_api_key" to (
                        System.getenv("DROP_BOX_API_KEY")
                            ?: gradleLocalProperties(
                                rootDir,
                                providers
                            ).getProperty("DROP_BOX_API_KEY").orEmpty()
                )
            )
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
