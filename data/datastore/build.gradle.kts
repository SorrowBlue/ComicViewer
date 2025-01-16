plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.datastore"
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(projects.domain.service)
        implementation(libs.androidx.datastore)
        implementation(libs.androidx.datastoreCoreOkio)
        implementation(libs.kotlinx.serialization.protobuf)
    }

    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.startup.runtime)
        implementation(libs.google.android.play.feature.delivery.ktx)
        implementation(libs.koin.androidxStartup)
    }
}
