plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.book"
    resourcePrefix("book")
}

dependencies {
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.foundation.android)
}
