@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.konan.properties.propertyString

plugins {
    id("build-logic.android.application")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.aboutlibraries.plugin)
    alias(libs.plugins.grgit)
}

fun String.toVersion() = this + if (matches(".*-[0-9]+-g[0-9a-f]{7}".toRegex())) "-SNAPSHOT" else ""

android {
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        versionCode = 12
        versionName = grgit.describe {
            longDescr = false
            isTags = true
        }?.toVersion() ?: "0.0.1-SNAPSHOT"
        logger.lifecycle("versionName=$versionName")
    }

    signingConfigs {
        getByName("debug") {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("debug.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("debug.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("debug.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("debug.keyPassword")
        }
        create("release") {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("release.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("release.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("release.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("release.keyPassword")
        }
        create("prerelease") {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("release.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("release.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("release.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("release.keyPassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        create("prerelease") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("prerelease")
        }
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    dynamicFeatures += setOf(
        ":dynamic",
        ":document",
        ":library:googledrive",
        ":library:onedrive",
        ":library:dropbox",
        ":library:box"
    )
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-core:2.13.4")
    api(libs.google.code.gson)
    api(libs.google.guava)
    api(projects.framework.ui)
    api(projects.framework.notification)
    api(projects.data.reader)

    implementation(projects.data.di)
    api(projects.domain)
    implementation(projects.settings)
    implementation(projects.folder)
    implementation(projects.file)
    implementation(projects.bookshelf)
    implementation(projects.favorite)
    implementation(projects.readlater)
    api(projects.library)
    implementation(projects.settings.security)

    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.splashscreen)

//    debugImplementation(libs.squareup.leakcanary.android)

    implementation(libs.dagger.hilt.android.core)
    kapt(libs.dagger.hilt.android.compiler)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

kapt {
    correctErrorTypes = true
}
