/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.convention

import com.sorrowblue.comicviewer.configureKotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

plugins {
    com.android.library
    com.autonomousapps.`dependency-analysis`
    id("comicviewer.primitive.lint")
    id("comicviewer.primitive.detekt")
    id("comicviewer.primitive.dokka")
    id("comicviewer.primitive.aboutlibraries")
}

kotlin {
    configureKotlin<KotlinAndroidProjectExtension>()
    compilerOptions {
        if (project.path.startsWith(":data")) {
            optIn.add("com.sorrowblue.comicviewer.domain.model.common.InternalDataApi")
        }
    }
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":framework:common"))
}
