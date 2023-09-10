package com.sorrowblue.buildlogic

import com.android.build.api.dsl.DynamicFeatureExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
internal class ComicViewerAndroidDynamicFeaturePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id("com.android.dynamic-feature")
                id("org.jetbrains.kotlin.android")
            }

            extensions.configure<DynamicFeatureExtension> {
                this.applyCommonConfigure(this@with)
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
                buildFeatures {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = libs.version("androidx-compose-compiler")
                }
            }

            kotlin {
                jvmToolchain(17)
            }
        }
    }
}
