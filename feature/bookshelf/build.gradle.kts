plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.designsystem)
                implementation(projects.framework.ui)
                implementation(projects.domain.model)
                implementation(projects.domain.usecase)
                implementation(projects.feature.file)

                implementation(libs.androidx.paging.common)
            }
        }

        androidMain {
            dependencies {
                implementation(projects.feature.bookshelf.edit)
                implementation(projects.feature.bookshelf.info)
                implementation(projects.feature.bookshelf.selection)
                implementation(projects.feature.file)
                implementation(projects.feature.folder)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf"
    resourcePrefix("bookshelf")
}
