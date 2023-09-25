plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.readlater"
    resourcePrefix("readlater")
}

dependencies {
    implementation(projects.framework)
    implementation(projects.feature.file)
    implementation(projects.feature.folder)
}
