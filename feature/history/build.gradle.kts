plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.history"
    resourcePrefix("history")
}

dependencies {
    implementation(projects.feature.file)
}
