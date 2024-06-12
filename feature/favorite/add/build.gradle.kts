plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite.add"
    resourcePrefix("favorite_add")
}

dependencies {
    implementation(projects.feature.favorite.common)
}
