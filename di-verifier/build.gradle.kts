plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                rootProject.subprojects.filterNot { it.name == project.name }.forEach {
                    val hasSource = it.projectDir.resolve("src").exists()
                    if (hasSource) {
                        implementation(it)
                    } else {
                        logger.lifecycle("Skipping empty or non-source module: ${it.name}")
                    }
                }
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.di_verifier"
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}
