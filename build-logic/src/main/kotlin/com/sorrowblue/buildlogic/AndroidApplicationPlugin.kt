package com.sorrowblue.buildlogic

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
internal class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id("com.android.application")
                id("org.jetbrains.kotlin.android")
            }
            extensions.configure<BaseAppModuleExtension> {
                applyCommonConfigure(target)
                defaultConfig {
                    targetSdk = libs.version("android-target-sdk").toInt()
                }
            }
            kotlin {
                jvmToolchain(17)
            }
        }
    }
}
