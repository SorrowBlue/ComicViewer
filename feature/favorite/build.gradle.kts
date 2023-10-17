plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite"
    resourcePrefix("favorite")
}

dependencies {
    implementation(projects.feature.favorite.common)
    implementation(projects.feature.file)
    implementation(projects.feature.favorite.edit)
    implementation(projects.feature.folder)
}
