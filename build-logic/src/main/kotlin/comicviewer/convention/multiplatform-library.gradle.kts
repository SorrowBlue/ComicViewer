package comicviewer.convention

import com.sorrowblue.comicviewer.libs
import desktopMain
import desktopTest

plugins {
    `kotlin-multiplatform`
    com.android.kotlin.multiplatform.library
    id("comicviewer.primitive.lint")
    id("comicviewer.primitive.detekt")
    id("comicviewer.primitive.dokka")
    id("comicviewer.primitive.aboutlibraries")
    id("comicviewer.primitive.licensee")
}

kotlin {
    androidLibrary {
        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.file("consumer-rules.pro")
        }
    }
    jvm("desktop")

    iosX64() // iPhone Simulator on 64-bit MacOS
    iosArm64() // 64-bit iPhone devices
    iosSimulatorArm64() // iPhone Simulator on Arm 64-bit MacOS

    applyDefaultHierarchyTemplate()

    sourceSets {
        val noAndroid by creating {
            dependsOn(commonMain.get())
        }
        val noAndroidTest by creating {
            dependsOn(commonTest.get())
        }
        desktopMain {
            dependsOn(noAndroid)
        }
        desktopTest {
            dependsOn(commonTest.get())
            dependsOn(noAndroidTest)
        }
        iosMain {
            dependsOn(noAndroid)
        }
        iosTest {
            dependsOn(commonTest.get())
            dependsOn(noAndroidTest)
        }
    }

    jvmToolchain {
        vendor.set(JvmVendorSpec.ADOPTIUM)
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
        val warningsAsErrors: String? by project
        allWarningsAsErrors.set(warningsAsErrors.toBoolean())
    }
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
