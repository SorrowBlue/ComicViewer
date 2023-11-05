plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.search"
    resourcePrefix("search")
}

dependencies {
    implementation(projects.feature.file)
    implementation(projects.feature.folder)
    detektPlugins("io.nlopez.compose.rules:detekt:0.3.3")
}
