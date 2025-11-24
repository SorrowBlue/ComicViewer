package com.sorrowblue.comicviewer

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureAndroid(extension: ApplicationExtension) {
    extension.apply {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        testOptions {
            unitTests {
                isIncludeAndroidResources = true
            }
        }

        buildTypes {
            create(ComicBuildType.PRERELEASE.display) {
                initWith(getByName(ComicBuildType.RELEASE.display))
            }
            create(ComicBuildType.INTERNAL.display) {
                initWith(getByName(ComicBuildType.RELEASE.display))
            }
        }
    }
}

internal fun Project.configureAndroid(extension: LibraryExtension) {
    extension.apply {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            consumerProguardFiles("consumer-rules.pro")
        }

        testOptions {
            unitTests {
                isIncludeAndroidResources = true
            }
        }

        buildTypes {
            create(ComicBuildType.PRERELEASE.display) {
                initWith(getByName(ComicBuildType.RELEASE.display))
            }
            create(ComicBuildType.INTERNAL.display) {
                initWith(getByName(ComicBuildType.RELEASE.display))
            }
        }
    }
}

internal fun Project.configureAndroid(extension: DynamicFeatureExtension) {
    extension.apply {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        testOptions {
            unitTests {
                isIncludeAndroidResources = true
            }
        }

        buildTypes {
            create(ComicBuildType.PRERELEASE.display) {
                initWith(getByName(ComicBuildType.RELEASE.display))
            }
            create(ComicBuildType.INTERNAL.display) {
                initWith(getByName(ComicBuildType.RELEASE.display))
            }
        }
    }
}
