plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.service"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)
                implementation(projects.domain.usecase)

                api(libs.squareup.okio)
                implementation(libs.androidx.paging.common)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
