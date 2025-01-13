plugins {
    alias(libs.plugins.comicviewer.android.kotlinMultiplatform)
    alias(libs.plugins.google.dagger.hilt)
    alias(libs.plugins.google.ksp)
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
                implementation(libs.google.dagger.hilt.android)
                implementation(libs.google.android.play.feature.delivery.ktx)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.google.dagger.hilt.compiler)
}
