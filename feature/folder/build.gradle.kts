plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.folder"
    resourcePrefix("folder")
}

dependencies {
    implementation(projects.feature.file)
}
