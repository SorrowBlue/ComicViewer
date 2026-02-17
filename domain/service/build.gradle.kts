plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.domain.service"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)
                implementation(projects.domain.usecase)

                api(libs.okio)
                implementation(libs.androidx.pagingCommon)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
