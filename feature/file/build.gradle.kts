plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.file"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.collection.nav)
                implementation(projects.feature.file.nav)
                implementation(projects.feature.folder.nav)
                implementation(libs.androidx.pagingCommon)
                implementation(libs.androidx.pagingCompose)
                implementation(libs.compose.material3Adaptive)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
