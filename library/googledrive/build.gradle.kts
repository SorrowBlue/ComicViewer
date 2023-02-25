@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("build-logic.android.dynamic-feature")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    resourcePrefix("googledrive")

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation(projects.app)
    implementation(projects.dynamic)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.google.http.client.gson)
    implementation(libs.google.api.client.android) {
        exclude("org.apache.httpcomponents")
    }
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.google.api.services.drive) {
        exclude("org.apache.httpcomponents")
    }
}

kapt {
    correctErrorTypes = true
}
