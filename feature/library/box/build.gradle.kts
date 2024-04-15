import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("comicviewer.android.feature.dynamic-feature")
    id("comicviewer.android.koin")
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.box"
    resourcePrefix("box")

    buildTypes {
        all {
            val localProperties = gradleLocalProperties(rootDir, providers)
            val boxClientId = System.getenv("BOX_CLIENT_ID")
                ?: localProperties.getProperty("BOX_CLIENT_ID").orEmpty()
            val boxClientSecret = System.getenv("BOX_CLIENT_SECRET")
                ?: localProperties.getProperty("BOX_CLIENT_SECRET").orEmpty()
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
