package com.sorrowblue.comicviewer

import desktopMain
import desktopTest
import org.gradle.api.Project
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Configure base Kotlin multiplatform options
 *
 * ```
 * common
 * ├ androidTarget
 * ├ jvm(desktop)
 * └ native
 *   └ apple
 *     └ ios
 *       ├ iosX64
 *       ├ iosArm64
 *       └ iosSimulatorArm64
 * ```
 */
internal fun Project.configureKotlinMultiplatform() {
    kotlin<KotlinMultiplatformExtension> {
        androidTarget {
        }

        jvm("desktop")

        iosX64() // iPhone Simulator on 64-bit MacOS
        iosArm64() // 64-bit iPhone devices
        iosSimulatorArm64() // iPhone Simulator on Arm 64-bit MacOS

        applyDefaultHierarchyTemplate()

        val noAndroid by sourceSets.creating {
            dependsOn(sourceSets.commonMain.get())
        }

        sourceSets.iosMain {
            dependsOn(noAndroid)
        }

        sourceSets.desktopMain {
            dependsOn(noAndroid)
        }

        val noAndroidTest by sourceSets.creating {
            dependsOn(sourceSets.commonTest.get())
        }

        sourceSets.iosTest {
            dependsOn(noAndroidTest)
        }

        sourceSets.desktopTest {
            dependsOn(noAndroidTest)
        }

        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }
}
