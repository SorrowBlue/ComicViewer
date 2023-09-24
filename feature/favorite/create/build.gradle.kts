plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite.create"
    resourcePrefix("favorite_create")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
}
