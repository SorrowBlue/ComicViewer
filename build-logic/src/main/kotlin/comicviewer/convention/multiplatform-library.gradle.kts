/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.convention

import com.sorrowblue.comicviewer.configureKotlin
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    org.jetbrains.kotlin.multiplatform
    com.android.kotlin.multiplatform.library
    com.autonomousapps.`dependency-analysis`
    id("comicviewer.primitive.aboutlibraries")
    id("comicviewer.primitive.detekt")
    id("comicviewer.primitive.dokka")
    id("comicviewer.primitive.lint")
    id("comicviewer.primitive.metro")
}

kotlin {
    android {
        @Suppress("UnstableApiUsage")
        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.file("consumer-rules.pro")
        }
    }
    jvm()
    iosArm64() // 64-bit iPhone devices
    iosSimulatorArm64() // iPhone Simulator on Arm 64-bit macOS
    applyDefaultHierarchyTemplate()

    sourceSets {
        val noAndroid = create("noAndroid") {
            dependsOn(commonMain.get())
        }
        val noAndroidTest = create("noAndroidTest") {
            dependsOn(commonTest.get())
        }
        jvmMain {
            dependsOn(noAndroid)
        }
        jvmTest {
            dependsOn(noAndroidTest)
        }
        iosMain {
            dependsOn(noAndroid)
        }
        iosTest {
            dependsOn(noAndroidTest)
        }
    }

    configureKotlin<KotlinMultiplatformExtension>()
    compilerOptions {
        if (project.path.startsWith(":data")) {
            optIn.add("com.sorrowblue.comicviewer.domain.model.common.InternalDataApi")
        }
    }
    sourceSets {
        commonMain.dependencies {
            if (project.path != ":core:logger") {
                implementation(project(":core:logger"))
            }
        }
    }
}

tasks.named<Test>("jvmTest") {
    val testSize = providers.gradleProperty("testSize").orNull
    val excludeTestSize = providers.gradleProperty("excludeTestSize").orNull
    if (testSize != null || excludeTestSize != null) {
        failOnNoDiscoveredTests.set(false)
    }
    useJUnitPlatform {
        testSize?.let { size ->
            includeTags(size)
        }
        excludeTestSize?.let { size ->
            excludeTags(size)
        }
    }
}
