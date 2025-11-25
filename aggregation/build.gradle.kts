plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.aggregation"
    }
    sourceSets {
        commonMain {
            dependencies {
                rootProject.subprojects.filterNot {
                    it.path == project.path || it.path.startsWith(projects.app.path)
                        || it.path == projects.data.reader.document.android.path
                        || it.path == projects.framework.notification.path
                }.forEach {
                    val hasSource = it.projectDir.resolve("src").exists()
                    if (hasSource) {
                        implementation(it)
                    } else {
                        logger.lifecycle("Skipping empty or non-source module: ${it.name}")
                    }
                }
                // Required for metro dependency resolution
                implementation(libs.androidx.datastore)
                implementation(projects.data.reader.zip)
            }
        }

        androidMain.dependencies {
            implementation(projects.data.reader.document.android)
        }
    }
}
