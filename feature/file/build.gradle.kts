plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)
                implementation(projects.framework.ui)
                implementation(projects.domain.model)
                implementation(projects.domain.usecase)
                implementation(libs.androidx.paging.common)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.file"
    resourcePrefix("file")
}
