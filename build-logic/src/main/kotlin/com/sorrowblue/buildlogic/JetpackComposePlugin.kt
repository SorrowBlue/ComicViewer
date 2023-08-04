package com.sorrowblue.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
internal class JetpackComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion =
                        libs.findVersion("androidx-compose-compiler").get().strictVersion
                }
            }
            dependencies {
                add("debugImplementation", libs.findBundle("androidx-compose-debug").get())
//                add("androidTestImplementation", platform(libs.findBundle("androidx-compose-bom").get()))
//                add("androidTestImplementation", libs.findBundle("androidx-compose-ui-test-junit4").get())
            }
        }
    }
}
