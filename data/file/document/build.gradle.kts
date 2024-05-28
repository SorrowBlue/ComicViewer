plugins {
    id("comicviewer.android.dynamic-feature")
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
    implementation(projects.data.file.reader)

    implementation(libs.androidx.core.ktx)
    implementation(libs.artifex.mupdf.fitz)
}
