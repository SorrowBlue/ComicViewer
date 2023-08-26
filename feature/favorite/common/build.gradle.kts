plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite.common"
    resourcePrefix("favorite_common")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
}
