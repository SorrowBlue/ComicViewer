plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.model"

    buildTypes {
        create("prerelease") {
            initWith(getByName("release"))
        }
        create("internal") {
            initWith(getByName("release"))
        }
    }
}

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(17)
    }
    androidTarget()
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.core)
            }
        }
    }
}
