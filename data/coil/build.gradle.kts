plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.atomicfu)
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(projects.domain.service)
        implementation(projects.domain.reader)
        implementation(libs.coil3)
        implementation(libs.kotlinx.serialization.json)
    }

    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.startup.runtime)
//        implementation(libs.androidx.exifinterface)
        implementation(libs.coil3.networkKtor)
        implementation(libs.koin.androidxStartup)
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.coil"
}
