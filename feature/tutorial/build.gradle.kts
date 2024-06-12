plugins {
    alias(libs.plugins.comicviewer.android.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.tutorial"
    resourcePrefix("tutorial")
}

dependencies {
    implementation(libs.google.android.play.feature.delivery.ktx)
}
