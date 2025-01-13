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

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

                implementation(projects.framework.common)
                implementation(libs.androidx.paging.common)
            }
        }
    }
}
