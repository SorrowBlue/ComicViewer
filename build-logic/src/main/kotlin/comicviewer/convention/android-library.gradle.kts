package comicviewer.convention

import com.sorrowblue.comicviewer.libs

plugins {
    com.android.library
    id("comicviewer.primitive.lint")
    id("comicviewer.primitive.detekt")
    id("comicviewer.primitive.dokka")
    id("comicviewer.primitive.aboutlibraries")
    id("comicviewer.primitive.licensee")
}

kotlin {
    jvmToolchain {
        vendor.set(JvmVendorSpec.ADOPTIUM)
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")

        freeCompilerArgs.add("-Xexpect-actual-classes")

        val warningsAsErrors: String? by project
        allWarningsAsErrors.set(warningsAsErrors.toBoolean())
        if (project.path.startsWith(":data")) {
            optIn.add("com.sorrowblue.comicviewer.domain.model.InternalDataApi")
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
