package com.sorrowblue.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
internal class AndroidDaggerHiltPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins {
                id("com.google.devtools.ksp")
                id("dagger.hilt.android.plugin")
            }
            dependencies {
                add("implementation", libs.findLibrary("google-dagger-hilt-android").get())
                add("ksp", libs.findLibrary("google.dagger-compiler").get())
                add("ksp", libs.findLibrary("google.dagger-hilt-compiler").get())
            }
        }
    }
}
