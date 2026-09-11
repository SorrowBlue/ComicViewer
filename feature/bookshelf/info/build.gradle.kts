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
                implementation(projects.framework.permission)
                implementation(projects.feature.bookshelf.nav)
                implementation(projects.framework.ui.file)
                implementation(libs.androidx.coreUri)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutinesTest)
            }
        }
    }
}
