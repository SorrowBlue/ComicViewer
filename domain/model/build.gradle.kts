plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.kotlin.plugin.parcelize)
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
}
