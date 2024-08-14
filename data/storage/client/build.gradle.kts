plugins {
    alias(libs.plugins.comicviewer.android.library)
    alias(libs.plugins.comicviewer.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.storage"
}

dependencies {
    api(projects.domain.service)
    api(projects.domain.model)
    api(projects.domain.reader)
}
