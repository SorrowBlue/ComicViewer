package com.sorrowblue.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
internal class AndroidLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
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
            }
            kotlin {
                jvmToolchain(17)
            }
        }
    }
}

