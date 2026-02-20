package comicviewer.convention

import com.sorrowblue.comicviewer.libs

plugins {
    org.jetbrains.kotlin.multiplatform
    com.android.kotlin.multiplatform.library
    id("comicviewer.primitive.lint")
    id("comicviewer.primitive.detekt")
    id("comicviewer.primitive.metro")
    id("comicviewer.primitive.dokka")
    id("comicviewer.primitive.aboutlibraries")
    id("comicviewer.primitive.licensee")
}

kotlin {
    androidLibrary {
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
