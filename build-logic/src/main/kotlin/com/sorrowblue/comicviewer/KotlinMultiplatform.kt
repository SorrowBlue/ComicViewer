package com.sorrowblue.comicviewer

import com.android.build.api.dsl.androidLibrary
import desktopMain
import desktopTest
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import sourceSets

/**
 * Configure base Kotlin multiplatform options
 *
 * ```
 * common
 * ├ android
 * ├ jvm(desktop)
 * └ native
 *   └ apple
 *     └ ios
 *       ├ iosX64
 *       ├ iosArm64
 *       └ iosSimulatorArm64
 * ```
 */
internal fun Project.configureKotlinMultiplatform(action: KotlinMultiplatformExtension.() -> Unit = {}) {
    configure<KotlinMultiplatformExtension> {
        jvmToolchain {
            vendor.set(JvmVendorSpec.ADOPTIUM)
            languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
        }

        if (pluginManager.hasPlugin(libs.plugins.android.kotlin.multiplatform.library)) {
            androidLibrary { }
        } else {
            androidTarget {
                publishAllLibraryVariants()
            }
        }
        jvm("desktop")
        iosX64() // iPhone Simulator on 64-bit MacOS
        iosArm64() // 64-bit iPhone devices
        iosSimulatorArm64() // iPhone Simulator on Arm 64-bit MacOS
        applyDefaultHierarchyTemplate()

        sourceSets {
            val noAndroid by creating {
                dependsOn(commonMain.get())
            }
            iosMain {
                dependsOn(noAndroid)
            }
            desktopMain {
                dependsOn(noAndroid)
            }
            val noAndroidTest by creating {
                dependsOn(commonTest.get())
            }
            iosTest {
                dependsOn(noAndroidTest)
            }
            desktopTest {
                dependsOn(noAndroidTest)
            }
        }

        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            val warningsAsErrors: String? by project
            allWarningsAsErrors.set(warningsAsErrors.toBoolean())
        }
        action()
    }
}
