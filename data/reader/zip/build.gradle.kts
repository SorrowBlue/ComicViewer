plugins {
    id("build-logic.android.library")
    id("com.sorrowblue.dagger-hilt")
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader.zip"
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data.reader)

    implementation(libs.androidx.startup.runtime)
    implementation(libs.github.omicronapps.sevenZipJBinding4Android)
}
