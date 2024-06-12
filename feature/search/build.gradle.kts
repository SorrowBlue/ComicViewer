plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.search"
    resourcePrefix("search")
}

dependencies {
    implementation(projects.feature.file)
    implementation(projects.feature.folder)
}
