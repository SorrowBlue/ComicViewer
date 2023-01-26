package com.sorrowblue.buildlogic

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
