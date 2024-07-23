plugins {
    alias(libs.plugins.comicviewer.android.featureDynamicFeature)
    alias(libs.plugins.comicviewer.koin)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.box"
    resourcePrefix("box")

    buildTypes {
        val comicviewerBoxClientId: String? by project
        val comicviewerBoxClientSecret: String? by project
        if (comicviewerBoxClientId.isNullOrEmpty()) {
            logger.warn("comicviewerBoxClientId is not set.")
        }
        if (comicviewerBoxClientSecret.isNullOrEmpty()) {
            logger.warn("comicviewerBoxClientSecret is not set.")
        }
        all {
            buildConfigField("String", "BOX_CLIENT_ID", "\"$comicviewerBoxClientId\"")
            buildConfigField("String", "BOX_CLIENT_SECRET", "\"$comicviewerBoxClientSecret\"")
        }
    }

    buildFeatures.buildConfig = true
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.domain.model)
    implementation(projects.feature.library)

    implementation(libs.androidx.browser)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.box.java.sdk)
    implementation(libs.kotlinx.serialization.protobuf)
}
