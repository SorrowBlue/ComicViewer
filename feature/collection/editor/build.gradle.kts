plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.soil.form)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.collection.editor"
    resourcePrefix("collection_editor")
}
