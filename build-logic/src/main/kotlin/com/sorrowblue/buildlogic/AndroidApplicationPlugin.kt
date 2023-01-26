package com.sorrowblue.buildlogic

@Suppress("unused")
internal class AndroidApplicationPlugin : AndroidCommonPlugin({
    plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
    }
    useAndroidExtension()

    kotlin {
        jvmToolchain(11)
    }
})
