@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.dynamic-feature")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    namespace = "com.sorrowblue.comicviewer.library.googledrive"
    resourcePrefix("googledrive")

    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            proguardFiles("proguard-rules-dynamic-features.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
}

kotlin.jvmToolchain(11)

dependencies {
    implementation(project(":app"))

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.google.http.client.gson)
    implementation(libs.google.api.client.android) {
        exclude("org.apache.httpcomponents")
    }
    implementation(libs.google.api.services.drive) {
        exclude("org.apache.httpcomponents")
    }
}

kapt {
    correctErrorTypes = true
}
