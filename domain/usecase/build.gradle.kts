plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.kotlinSerialization)
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
                implementation(libs.androidx.pagingCommon)
            }
        }
    }
}
