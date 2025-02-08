plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
}

group = "com.sorrowblue"
version = "1.0"
publishing {
    repositories {
        mavenLocal()
    }
}
kotlin {
    jvm {
        withSourcesJar()
    }
    androidTarget {
        publishLibraryVariants("release")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)

                implementation(libs.koin.core)

                implementation(libs.koin.annotations)

                implementation(libs.squareup.okio)
                implementation(libs.kotlinx.serialization.jsonOkio)
                api("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha12")
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.annotations)
            }
        }
//        androidMain {
//            dependencies {
//                implementation("androidx.navigation:navigation-compose:2.9.0-alpha05")
//            }
//        }
    }
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.navigation"
}
