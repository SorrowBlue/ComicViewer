plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.file"
    resourcePrefix("file")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
}
