plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.favorite.common)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite.add"
    resourcePrefix("favorite_add")
}
