package com.sorrowblue.buildlogic

import com.android.build.api.dsl.DynamicFeatureExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
internal class AndroidDynamicFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id("com.android.dynamic-feature")
                id("org.jetbrains.kotlin.android")
            }
            extensions.configure<DynamicFeatureExtension> {
                applyCommonConfigure(target)
                buildTypes {
                    release {
                        proguardFile("consumer-rules.pro")
                    }
                    debug {
                        proguardFile("consumer-rules.pro")
                    }
                    prerelease {
                        proguardFile("consumer-rules.pro")
                    }
                    internal {
                        proguardFile("consumer-rules.pro")
                    }
                }
            }
            kotlin {
                jvmToolchain(17)
            }
        }
    }
}
