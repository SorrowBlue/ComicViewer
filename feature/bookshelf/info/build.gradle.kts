plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf.info"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.bookshelf.nav)
                implementation(projects.feature.permission.nav)
                implementation(projects.framework.permission)
                implementation(libs.androidx.pagingCommon)
                implementation(libs.androidx.pagingCompose)
                implementation(libs.compose.material3Adaptive)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutinesTest)
            }
        }
    }
}
