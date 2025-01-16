plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(projects.domain.service)
        api(projects.domain.model)
        api(projects.domain.reader)
        implementation(libs.kotlinx.coroutines.core)
    }

    sourceSets.androidMain.dependencies {
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.storage"
}
