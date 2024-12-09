package com.sorrowblue.comicviewer

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal inline fun <reified T : CommonExtension<*, *, *, *, *, *>> Project.configureAndroidCompose() =
    configure<T> {
        buildFeatures.compose = true
    }
