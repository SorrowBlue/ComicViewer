plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                rootProject.subprojects.filterNot { it.name == project.name || it.name == projects.composeApp.name}.forEach {
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
    }

    android {
        namespace = "com.sorrowblue.comicviewer.aggregation"
    }
}
