import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.comicviewer.android.featureDynamicFeature)
    alias(libs.plugins.comicviewer.koin)
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.box"
    resourcePrefix("box")

    buildTypes {
        val localProperties = gradleLocalProperties(rootDir, providers)
        val boxClientId = System.getenv("BOX_CLIENT_ID")
            ?: localProperties.getProperty("BOX_CLIENT_ID")
        if (boxClientId.isNullOrEmpty()) {
            logger.warn("BOX_CLIENT_ID is not set.")
        }
        val boxClientSecret = System.getenv("BOX_CLIENT_SECRET")
            ?: localProperties.getProperty("BOX_CLIENT_SECRET")
        if (boxClientSecret.isNullOrEmpty()) {
            logger.warn("BOX_CLIENT_SECRET is not set.")
        }
        all {
            buildConfigField("String", "BOX_CLIENT_ID", "\"$boxClientId\"")
            buildConfigField("String", "BOX_CLIENT_SECRET", "\"$boxClientSecret\"")
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
