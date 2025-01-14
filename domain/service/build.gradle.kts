plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.service"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)
                implementation(projects.domain.usecase)
                implementation(projects.domain.reader)
                implementation(libs.androidx.paging.common)
                implementation(libs.kotlinx.datetime)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.google.android.play.feature.delivery.ktx)
            }
        }
    }
}
