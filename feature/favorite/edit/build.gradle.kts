plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite.edit"
    resourcePrefix("favorite_edit")
}

dependencies {
    implementation(projects.feature.favorite.common)
}
