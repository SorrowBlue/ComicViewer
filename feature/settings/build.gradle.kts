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
                implementation(projects.feature.settings.common)
                implementation(projects.feature.settings.display)
                implementation(projects.feature.settings.folder)
                implementation(projects.feature.settings.info)
                implementation(projects.feature.settings.security)
                implementation(projects.feature.settings.viewer)
                implementation(projects.feature.authentication)
                implementation(projects.feature.tutorial)
                implementation(compose.material3AdaptiveNavigationSuite)
                implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.1.0-alpha02")
                api("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.1.0-alpha02")
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.google.android.billingclient.billingKtx)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings"
    resourcePrefix("settings")
}

dependencies {
    add("kspCommonMainMetadata", projects.framework.navigation.kspCompiler)
    add("kspAndroid", projects.framework.navigation.kspCompiler)
    add("kspIosX64", projects.framework.navigation.kspCompiler)
    add("kspIosArm64", projects.framework.navigation.kspCompiler)
    add("kspIosSimulatorArm64", projects.framework.navigation.kspCompiler)
    add("kspDesktop", projects.framework.navigation.kspCompiler)
}
