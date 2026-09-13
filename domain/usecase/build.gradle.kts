plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.domain.usecase"
        withHostTest {}
    }
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)
                implementation(projects.domain.repository)
                implementation(projects.domain.service)
                api(libs.androidx.pagingCommon)
                api(libs.kotlinx.coroutinesCore)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
