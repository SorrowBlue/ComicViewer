plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.feature.collection"
//        resourcePrefix("collection")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
                implementation(projects.feature.collection.add)
                implementation(projects.feature.collection.editor)
                implementation(libs.soil.form)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.compose.animation.graphics)
            }
        }
    }
}
