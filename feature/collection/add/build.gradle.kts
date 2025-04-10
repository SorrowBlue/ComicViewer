plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.collection.add"
    resourcePrefix("collection_add")
}
