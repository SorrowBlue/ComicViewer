plugins {
    alias(libs.plugins.comicviewer.android.dynamicFeature)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader.document"
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    implementation(projects.app)
    implementation(projects.data.storage.client)

    implementation(libs.androidx.core.ktx)
    implementation(libs.artifex.mupdf.fitz)
    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.2.0")

    // NOTE: It's important that you _don't_ use compileOnly here, as it will fail to resolve at compile-time otherwise
    implementation("com.google.auto.service:auto-service-annotations:1.1.1")
}
