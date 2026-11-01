/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** Configure base Kotlin options */
inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() = configure<T> {
    jvmToolchain {
        vendor.set(JvmVendorSpec.ADOPTIUM)
        languageVersion.set(libs.versions.java.map(JavaLanguageVersion::of))
    }
    require(this is KotlinAndroidProjectExtension || this is KotlinMultiplatformExtension) {
        "Unsupported project extension $this ${T::class}"
    }
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xexplicit-context-arguments")
        freeCompilerArgs.add("-Xcollection-literals")
        freeCompilerArgs.add("-Xintrinsic-const-evaluation")
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")

        allWarningsAsErrors.set(
            providers.gradleProperty("warningsAsErrors")
                .map(String::toBoolean)
                .orElse(false),
        )
    }
}
