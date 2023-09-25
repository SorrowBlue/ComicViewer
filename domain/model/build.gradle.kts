plugins {
    id("comicviewer.android.library")
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.common"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
}
