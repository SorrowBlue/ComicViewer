/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package comicviewer.convention

import com.sorrowblue.comicviewer.libs

plugins {
    org.jetbrains.kotlin.multiplatform
    org.jetbrains.kotlin.plugin.serialization
    com.google.devtools.ksp
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework:navigation"))
                implementation(libs.androidx.navigation3Runtime)
            }
        }
    }
}

dependencies {
    add("kspAndroid", project(":framework:navigation:processor"))
    add("kspJvm", project(":framework:navigation:processor"))
    add("kspJvmTest", project(":framework:navigation:processor"))
    add("kspIosArm64", project(":framework:navigation:processor"))
    add("kspIosArm64Test", project(":framework:navigation:processor"))
    add("kspIosSimulatorArm64", project(":framework:navigation:processor"))
    add("kspIosSimulatorArm64Test", project(":framework:navigation:processor"))
}
