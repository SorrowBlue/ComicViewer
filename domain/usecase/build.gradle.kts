plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.usecase"
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
                api(projects.domain.model)

                implementation(projects.framework.common)
                implementation(libs.androidx.paging.common)
            }
        }
    }
}
