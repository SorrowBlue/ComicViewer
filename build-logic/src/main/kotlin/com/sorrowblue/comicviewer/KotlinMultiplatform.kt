package com.sorrowblue.comicviewer

import desktopMain
import desktopTest
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Configure base Kotlin multiplatform options
 *
 * ```
 * common
 * ├ jvm(desktop)
 * └ native
 *   └ apple
 *     └ ios
 *       ├ iosX64
 *       ├ iosArm64
 *       └ iosSimulatorArm64
 * ```
 */
fun Project.configureKotlinMultiplatformSourceSets() {
    configure<KotlinMultiplatformExtension> {
        jvm("desktop")

        iosX64() // iPhone Simulator on 64-bit MacOS
        iosArm64() // 64-bit iPhone devices
        iosSimulatorArm64() // iPhone Simulator on Arm 64-bit MacOS

        applyDefaultHierarchyTemplate()

        sourceSets {
            val noAndroid by creating {
                dependsOn(sourceSets.commonMain.get())
            }
            val noAndroidTest by creating {
                dependsOn(sourceSets.commonTest.get())
            }
            desktopMain {
                dependsOn(noAndroid)
            }
            desktopTest {
                dependsOn(noAndroidTest)
            }
            iosMain {
                dependsOn(noAndroid)
            }
            iosTest {
                dependsOn(noAndroidTest)
            }
        }
    }
}
