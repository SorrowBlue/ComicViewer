plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.folder"
    resourcePrefix("folder")
}

dependencies {
    implementation(projects.framework)
    implementation(projects.feature.file)

    implementation(libs.androidx.work.runtime.ktx)
}
