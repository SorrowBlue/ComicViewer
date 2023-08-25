plugins {
    id("com.sorrowblue.android-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.search"
    resourcePrefix("search")
}

dependencies {	
    implementation(projects.framework.compose)
    implementation(projects.domain)
}
