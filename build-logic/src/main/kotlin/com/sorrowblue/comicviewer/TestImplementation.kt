package com.sorrowblue.comicviewer

import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope


context(Project)
fun DependencyHandlerScope.applyTestImplementation() {
    testImplementation(libs.androidx.test.ext.junitKtx)
    testImplementation(libs.androidx.test.ext.truth)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.ext.junitKtx)
    androidTestImplementation(libs.androidx.test.ext.truth)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
