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
                implementation(projects.framework.navigation.annotations)
                implementation(projects.framework.ui)
                implementation(projects.feature.file)
                implementation(projects.domain.model)
                implementation(projects.domain.usecase)
            }
        }

        androidMain {
            dependencies {
                implementation(projects.framework.notification)

                implementation(libs.androidx.work.runtime)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.info"
    resourcePrefix("bookshelf_info")
}

dependencies {
    add("kspCommonMainMetadata", projects.framework.navigation.kspCompiler)
    add("kspAndroid", projects.framework.navigation.kspCompiler)
    add("kspIosX64", projects.framework.navigation.kspCompiler)
    add("kspIosArm64", projects.framework.navigation.kspCompiler)
    add("kspIosSimulatorArm64", projects.framework.navigation.kspCompiler)
    add("kspDesktop", projects.framework.navigation.kspCompiler)
}


// Trigger Common Metadata Generation from Native tasks
//project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
//    if (name != "kspCommonMainKotlinMetadata") {
//        dependsOn("kspCommonMainKotlinMetadata")
//    }
//}
