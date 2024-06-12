package com.sorrowblue.comicviewer

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlin(extension: KotlinAndroidProjectExtension) {
    with(extension) {
        jvmToolchain {
            vendor.set(JvmVendorSpec.ADOPTIUM)
            languageVersion.set(JavaLanguageVersion.of(17))
        }
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-receivers")
        }
    }
}

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    with(commonExtension) {

        lint {
            baseline = file("lint-baseline.xml")
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
