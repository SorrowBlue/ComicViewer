plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.domain.usecase"
    }
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
