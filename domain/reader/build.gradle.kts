plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader"
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
                api(libs.squareup.okio)
            }
        }
    }
}
