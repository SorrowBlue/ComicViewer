plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
                implementation(projects.feature.favorite.edit)
                implementation(projects.feature.favorite.common)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.favorite"
    resourcePrefix("favorite")
}
