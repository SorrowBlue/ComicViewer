plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.book"
    resourcePrefix("book")
}

dependencies {
    implementation(projects.framework)
}
