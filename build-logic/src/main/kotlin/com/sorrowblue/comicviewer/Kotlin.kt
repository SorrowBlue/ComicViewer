package com.sorrowblue.comicviewer

import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** Configure base Kotlin options */
internal inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() =
    configure<T> {
        jvmToolchain {
            vendor.set(JvmVendorSpec.ADOPTIUM)
            languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
        }
        val warningsAsErrors: String? by project
        when (this) {
            is KotlinAndroidProjectExtension -> compilerOptions
            is KotlinMultiplatformExtension -> compilerOptions
            else -> throw UnsupportedOperationException("Unsupported project extension $this ${T::class}")
        }.allWarningsAsErrors.set(warningsAsErrors.toBoolean())
    }
