plugins {
    alias(libs.plugins.comicviewer.android.kotlinMultiplatform)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.usecase"
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)

                implementation(projects.framework.common)
                implementation(libs.androidx.paging.common)
            }
        }
    }
}
