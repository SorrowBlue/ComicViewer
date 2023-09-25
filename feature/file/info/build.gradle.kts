plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.file.info"
    resourcePrefix("file_info")
}

dependencies {
    implementation(projects.feature.favorite.add)
    implementation(projects.feature.folder)

    implementation(libs.google.accompanist.navigation.material)
}
