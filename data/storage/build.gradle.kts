plugins {
    id("comicviewer.android.library")
    id("comicviewer.android.hilt")
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.storage"
}

dependencies {
    implementation(projects.framework)
    implementation(projects.data.infrastructure)
    api(projects.data.model)
    api(projects.data.reader)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
