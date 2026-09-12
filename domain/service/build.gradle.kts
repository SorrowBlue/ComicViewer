plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.domain.service"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)
                implementation(projects.domain.repository)
                implementation(projects.domain.usecase)
                api(libs.kotlinx.coroutinesCore)
                api(libs.androidx.pagingCommon)
            }
        }
    }
}
