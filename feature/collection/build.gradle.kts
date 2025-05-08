plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
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
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.collection"
    resourcePrefix("collection")
}
