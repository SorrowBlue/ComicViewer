package com.sorrowblue.buildlogic

import com.android.build.api.dsl.DynamicFeatureExtension
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
internal class AndroidLibraryPlugin : AndroidCommonPlugin({
    plugins {
        id("com.android.library")
        id("org.jetbrains.kotlin.android")
    }
    useAndroidExtension()
    useAndroidLibraryExtension()
    kotlin {
        jvmToolchain(11)
    }
})

@Suppress("unused")
internal class AndroidDynamicFeaturePlugin : AndroidCommonPlugin({
    plugins {
        id("com.android.dynamic-feature")
        id("org.jetbrains.kotlin.android")
    }
    useAndroidExtension()

    extensions.configure<DynamicFeatureExtension> {
        buildTypes {
            getByName("release") {
                proguardFiles("consumer-rules.pro")
            }
            getByName("debug") {
                proguardFiles("consumer-rules.pro")
            }
            create("prerelease") {
                proguardFiles("consumer-rules.pro")
            }
        }
    }

    kotlin {
        jvmToolchain(11)
    }
})
