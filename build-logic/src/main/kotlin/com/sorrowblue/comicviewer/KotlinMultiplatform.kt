package com.sorrowblue.comicviewer

import org.gradle.api.Project
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
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
            publishAllLibraryVariants()
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

        val desktopMain by sourceSets.getting {
            dependsOn(noAndroid)
        }

        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }
}
