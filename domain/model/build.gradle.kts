plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
}
