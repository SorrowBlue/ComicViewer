plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.usecase"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)

                implementation(libs.kotlinx.datetime)
                implementation(libs.androidx.paging.common)
            }
        }
    }
}
