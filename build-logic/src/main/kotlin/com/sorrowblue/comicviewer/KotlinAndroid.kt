package com.sorrowblue.comicviewer

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun configureKotlin(extension: KotlinAndroidProjectExtension) {
    with(extension) {
        jvmToolchain {
            vendor.set(JvmVendorSpec.ADOPTIUM)
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    with(commonExtension) {

        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        @Suppress("UnstableApiUsage")
        testOptions {
            unitTests {
                isIncludeAndroidResources = true
            }
        }

        lint {
            checkAllWarnings = true
            checkDependencies = true
            disable += "InvalidPackage"
            baseline = file("lint-baseline.xml")
            htmlReport = true
            sarifReport = true
            textReport = false
            xmlReport = false
        }

        buildTypes {
            create("prerelease") {
                initWith(getByName("release"))
            }
            create("internal") {
                initWith(getByName("release"))
            }
        }

    }
}
