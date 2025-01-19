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
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.book"
    resourcePrefix("book")
}

dependencies {
    add("kspCommonMainMetadata", projects.framework.navigation.kspCompiler)
    add("kspAndroid", projects.framework.navigation.kspCompiler)
    add("kspIosX64", projects.framework.navigation.kspCompiler)
    add("kspIosArm64", projects.framework.navigation.kspCompiler)
    add("kspIosSimulatorArm64", projects.framework.navigation.kspCompiler)
    add("kspDesktop", projects.framework.navigation.kspCompiler)
}
