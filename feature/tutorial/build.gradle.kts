plugins {
    id("comicviewer.android.feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.tutorial"
    resourcePrefix("tutorial")
}

dependencies {
    implementation(libs.google.android.play.feature.delivery.ktx)
    implementation(libs.google.accompanist.pagerIndicators)
}
