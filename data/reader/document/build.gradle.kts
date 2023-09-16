plugins {
    id("build-logic.android.dynamic-feature")
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
    implementation(projects.data.reader)

    implementation(libs.androidx.core.ktx)
    implementation(libs.artifex.mupdf.fitz)
}
