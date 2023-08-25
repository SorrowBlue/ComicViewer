package com.sorrowblue.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
internal class ComicViewerAndroidFeaturePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
                id("org.jetbrains.kotlin.kapt")
                id("dagger.hilt.android.plugin")
            }

            extensions.configure<LibraryExtension> {
                this.applyCommonConfigure(this@with)
                buildTypes {
                    release {
                        consumerProguardFiles("consumer-rules.pro")
                    }
                    debug {
                        consumerProguardFiles("consumer-rules.pro")
                    }
                    prerelease {
                        consumerProguardFiles("consumer-rules.pro")
                    }
                    internal {
                        consumerProguardFiles("consumer-rules.pro")
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

            dependencies {
                add("implementation", libs.findLibrary("dagger-hilt-android-core").get())
                add("kapt", libs.findLibrary("dagger-hilt-android-compiler").get())
            }

            kapt {
                correctErrorTypes = true
            }
        }
    }
}
