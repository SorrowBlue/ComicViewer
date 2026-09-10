plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.domain.repository"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)
                api(libs.androidx.pagingCommon)
                api(libs.kotlinx.coroutinesCore)
            }
        }
    }
}
