package com.sorrowblue.comicviewer

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSourceSetConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

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
        }
    }
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
val NamedDomainObjectContainer<KotlinSourceSet>.desktopMain: NamedDomainObjectProvider<KotlinSourceSet> by KotlinSourceSetConvention

@OptIn(ExperimentalKotlinGradlePluginApi::class)
val NamedDomainObjectContainer<KotlinSourceSet>.desktopTest: NamedDomainObjectProvider<KotlinSourceSet> by KotlinSourceSetConvention
