package comicviewer.convention

import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    org.jetbrains.kotlin.multiplatform
    com.android.kotlin.multiplatform.library
    id("comicviewer.primitive.lint")
    id("comicviewer.primitive.detekt")
    id("comicviewer.primitive.metro")
    id("comicviewer.primitive.dokka")
    id("comicviewer.primitive.aboutlibraries")
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
        val noAndroid by creating {
            dependsOn(commonMain.get())
        }
        val noAndroidTest by creating {
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
            freeCompilerArgs.add(
                "-opt-in=com.sorrowblue.comicviewer.domain.model.InternalDataApi",
            )
        }
    }
    sourceSets {
        commonMain.dependencies {
            if (project.path != ":framework:common") {
                implementation(project(":framework:common"))
            }
        }
    }
}
