@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.sorrowblue.buildlogic.debug
import com.sorrowblue.buildlogic.internal
import com.sorrowblue.buildlogic.prerelease
import com.sorrowblue.buildlogic.projectString
import com.sorrowblue.buildlogic.release
import org.jetbrains.kotlin.konan.properties.propertyString

plugins {
    id("build-logic.android.application")
    id("com.sorrowblue.dagger-hilt")
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.mikepenz.aboutlibraries.plugin)
    alias(libs.plugins.grgit)
}

fun String.toVersion() = this + if (matches(".*-[0-9]+-g[0-9a-f]{7}".toRegex())) "-SNAPSHOT" else ""
val gitVersion = providers.exec {
    commandLine("git", "--version")
}.standardOutput.asText.get()
android {
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        versionCode = 12
        println("gitVersion=$gitVersion")
        versionName = grgit.describe {
            longDescr = false
            isTags = true
        }?.toVersion() ?: "0.0.1-SNAPSHOT"
        logger.lifecycle("versionName=$versionName")
    }

    signingConfigs {
        release {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("release.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("release.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("release.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("release.keyPassword")
        }
        prerelease {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("release.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("release.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("release.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("release.keyPassword")
        }
        internal {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("release.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("release.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("release.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("release.keyPassword")
        }
        debug {
            storeFile =
                file(gradleLocalProperties(rootDir).propertyString("debug.storeFile").orEmpty())
            storePassword = gradleLocalProperties(rootDir).propertyString("debug.storePassword")
            keyAlias = gradleLocalProperties(rootDir).propertyString("debug.keyAlias")
            keyPassword = gradleLocalProperties(rootDir).propertyString("debug.keyPassword")
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
        prerelease {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("prerelease")
        }
        internal {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("internal")
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
        projects.data.reader.document.projectString(),
        projects.library.box.projectString(),
        projects.library.dropbox.projectString(),
        projects.library.googledrive.projectString(),
        projects.library.onedrive.projectString(),
    )
}

dependencies {
    api(libs.google.code.gson)
    api(libs.androidx.browser)
    api("com.fasterxml.jackson.core:jackson-core:2.15.1")
    api(libs.google.guava)
    implementation(projects.framework.ui)
    implementation(projects.framework.notification)

    implementation(projects.data.di)
    implementation(projects.domain)
    implementation(projects.settings)
    implementation(projects.folder)
    implementation(projects.file)
    implementation(projects.bookshelf)
    implementation(projects.favorite)
    implementation(projects.readlater)
    implementation(projects.library)
    implementation(projects.settings.security)
    implementation(projects.settings.feature)

    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.splashscreen)

    debugImplementation(libs.squareup.leakcanary.android)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
