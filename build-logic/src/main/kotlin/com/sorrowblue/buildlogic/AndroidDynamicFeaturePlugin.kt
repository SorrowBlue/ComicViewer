package com.sorrowblue.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidDynamicFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.dynamic-feature")
                apply("org.jetbrains.kotlin.android")
            }
            useAndroidExtension()
            useAndroidLibraryExtension()
        }
    }
}
