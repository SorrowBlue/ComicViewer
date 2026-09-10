plugins {
    alias(libs.plugins.comicviewer.multiplatformFeature)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.feature.bookshelf.nav"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.model)
        }
    }
}
