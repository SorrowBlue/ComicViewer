package com.sorrowblue.comicviewer

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.dependTestImplementation() {
    dependencies {
        testImplementation(libs.findLibrary("androidx-compose-ui-testJunit4").get())
        testImplementation(libs.findLibrary("androidx-test-ext-junit-ktx").get())
        testImplementation(libs.findLibrary("androidx-test-ext-truth").get())
        testImplementation(libs.findLibrary("kotlinx-coroutines-test").get())
        testImplementation(libs.findLibrary("robolectric").get())
    }
}

fun CommonExtension<*, *, *, *, *, *>.testOption() {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}
