package com.sorrowblue.comicviewer

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryDefaultConfig
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal inline fun <reified T : CommonExtension<*, *, *, *, *, *>> Project.configureAndroid() =
    configure<T> {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            if (this is LibraryDefaultConfig) {
                consumerProguardFiles("consumer-rules.pro")
            } else if (this is ApplicationDefaultConfig) {
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }

        @Suppress("UnstableApiUsage")
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
