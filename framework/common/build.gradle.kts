plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.common"

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
    androidTarget {
        publishAllLibraryVariants()
    }
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                api(projects.domain.model)

                implementation(libs.androidx.paging.common)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.startup.runtime)
            }
        }
    }
}
