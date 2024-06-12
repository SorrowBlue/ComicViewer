plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite"
    resourcePrefix("favorite")
}

dependencies {
    implementation(projects.feature.file)
    implementation(projects.feature.folder)
    implementation(projects.feature.favorite.edit)
    implementation(projects.feature.favorite.common)
}
