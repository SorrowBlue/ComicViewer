import org.gradle.api.internal.catalog.DelegatingProjectDependency

plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                val dependencyHandler = this
                val skipModule = listOf(
                    projects.composeApp,
                    projects.data,
                    projects.data.di,
                    projects.data.reader,
                    projects.data.reader.document,
                    projects.data.storage,
                    projects.domain,
                    projects.feature,
                    projects.framework,
                    projects.framework.notification,
                ).map(DelegatingProjectDependency::getPath)
                rootProject.subprojects {
                    if (!skipModule.contains(project.path)) {
                        dependencyHandler.implementation(project)
                    }
                }
            }
        }

        desktopMain.dependencies {
            implementation(projects.data.reader.document)
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.di"
}
