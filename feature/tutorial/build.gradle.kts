plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.tutorial"
    resourcePrefix("tutorial")
}

dependencies {
    implementation(projects.framework.compose)
    implementation(projects.domain)
    implementation(libs.google.android.play.feature.delivery.ktx)
    implementation("com.google.accompanist:accompanist-pager-indicators:0.32.0")
}
