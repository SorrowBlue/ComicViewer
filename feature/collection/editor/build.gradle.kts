plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.collection.editor"
//        resourcePrefix("collection_editor")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.soil.form)
            }
        }
    }
}
