package comicviewer.convention

import com.sorrowblue.comicviewer.libs

plugins {
    com.android.application
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
        val warningsAsErrors: String? by project
        allWarningsAsErrors.set(warningsAsErrors.toBoolean())
    }
}

android {
    defaultConfig {
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro",
        )
    }
    signingConfigs {
        val androidSigningDebugStoreFile: String? by project
        if (!androidSigningDebugStoreFile.isNullOrEmpty()) {
            maybeCreate("debug").apply {
                val androidSigningDebugStorePassword: String? by project
                val androidSigningDebugKeyAlias: String? by project
                val androidSigningDebugKeyPassword: String? by project
                storeFile = file(requireNotNull(androidSigningDebugStoreFile))
                storePassword = androidSigningDebugStorePassword
                keyAlias = androidSigningDebugKeyAlias
                keyPassword = androidSigningDebugKeyPassword
            }
        } else {
            logger.warn("androidSigningDebugStoreFile not found")
        }

        val androidSigningReleaseStoreFile: String? by project
        if (!androidSigningReleaseStoreFile.isNullOrEmpty()) {
            maybeCreate("release").apply {
                val androidSigningReleaseStorePassword: String? by project
                val androidSigningReleaseKeyAlias: String? by project
                val androidSigningReleaseKeyPassword: String? by project
                storeFile = file(requireNotNull(androidSigningReleaseStoreFile))
                storePassword = androidSigningReleaseStorePassword
                keyAlias = androidSigningReleaseKeyAlias
                keyPassword = androidSigningReleaseKeyPassword
            }
        } else {
            logger.warn("androidSigningReleaseStoreFile not found")
        }
    }
}

dependencies {
    implementation(project(":framework:common"))
}

aboutLibraries {
    export {
        outputFile.set(file("src/main/res/raw/aboutlibraries.json"))
    }
}
