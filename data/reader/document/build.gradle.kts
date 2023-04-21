plugins {
    id("build-logic.android.dynamic-feature")
}

android {
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    implementation(projects.app)
    implementation(projects.data.reader)

    implementation(libs.squareup.logcat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.artifex.mupdf.fitz)
}
