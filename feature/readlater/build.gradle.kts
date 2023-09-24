plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.readlater"
    resourcePrefix("readlater")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
    implementation(projects.feature.file)
    implementation(projects.feature.folder)
}
