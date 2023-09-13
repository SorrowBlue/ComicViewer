plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite.edit"
    resourcePrefix("favorite_edit")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
}
