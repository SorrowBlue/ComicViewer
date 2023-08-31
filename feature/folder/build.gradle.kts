plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.folder"
    resourcePrefix("folder")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
    implementation(projects.feature.file)

    implementation(libs.androidx.work.runtime.ktx)
}
