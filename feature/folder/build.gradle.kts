plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.folder"
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.book.nav)
                implementation(projects.feature.folder.nav)
                implementation(projects.feature.permission.nav)
                implementation(projects.feature.search.nav)
                implementation(projects.feature.settings.nav)
                implementation(projects.feature.file.nav)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutinesTest)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
    }
}
