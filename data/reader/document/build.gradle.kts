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

    implementation(libs.google.autoServiceAnnotations)
    ksp(libs.autoservice.ksp)
}
