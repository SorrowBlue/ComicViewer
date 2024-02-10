package com.sorrowblue.comicviewer

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        kotlinOptions {
            freeCompilerArgs += "-Xcontext-receivers"
            jvmTarget = "17"
        }

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
