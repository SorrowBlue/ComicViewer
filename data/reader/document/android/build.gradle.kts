plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.metro)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader.document.android"
    buildFeatures {
        aidl = true
    }
}

kotlin {
    jvmToolchain {
        vendor.set(JvmVendorSpec.ADOPTIUM)
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
    compilerOptions {
        allWarningsAsErrors.set(false)
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
    }
}

dependencies {
    implementation(projects.data.storage.client)
    implementation(projects.framework.common)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startupRuntime)
}
