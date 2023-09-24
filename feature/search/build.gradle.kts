plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.search"
    resourcePrefix("search")
}

dependencies {
    implementation(projects.domain)
    implementation(projects.feature.folder)
}
