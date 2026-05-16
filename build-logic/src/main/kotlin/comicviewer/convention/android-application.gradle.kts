package comicviewer.convention

import com.sorrowblue.comicviewer.libs

plugins {
    com.android.application
    id("comicviewer.primitive.lint")
    id("comicviewer.primitive.detekt")
    id("comicviewer.primitive.dokka")
    id("comicviewer.primitive.aboutlibraries")
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
        listOf("debug", "release").forEach { configName ->
            val capitalizedName = configName.replaceFirstChar { it.uppercase() }
            val storeFilePath = (project.findProperty("androidSigning${capitalizedName}StoreFile")
                ?: project.findProperty("androidSigningStoreFile")) as String?
            if (!storeFilePath.isNullOrEmpty()) {
                maybeCreate(configName).apply {
                    storeFile = file(storeFilePath)
                    storePassword = (project.findProperty("androidSigning${capitalizedName}StorePassword")
                        ?: project.findProperty("androidSigningStorePassword")) as String?
                    keyAlias = (project.findProperty("androidSigning${capitalizedName}KeyAlias")
                        ?: project.findProperty("androidSigningKeyAlias")) as String?
                    keyPassword = (project.findProperty("androidSigning${capitalizedName}KeyPassword")
                        ?: project.findProperty("androidSigningKeyPassword")) as String?
                }
            } else {
                logger.warn("Store file is not found.(androidSigning${capitalizedName}StoreFile or androidSigningStoreFile)")
            }
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
