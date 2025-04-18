plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
                implementation(libs.soil.form)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.search"
    resourcePrefix("search")
}
