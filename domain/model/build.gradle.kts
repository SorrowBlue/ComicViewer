plugins {
    id("comicviewer.android.library")
    id("org.jetbrains.kotlin.plugin.parcelize")
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
}
