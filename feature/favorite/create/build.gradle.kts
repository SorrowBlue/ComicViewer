plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite.create"
    resourcePrefix("favorite_create")
}

dependencies {
    implementation(projects.feature.favorite.common)
}
