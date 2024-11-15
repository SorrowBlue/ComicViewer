plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.sorrowblue.comicviewer.framework.common"

    lint {
        checkAllWarnings = true
        checkDependencies = true
        enable += "WrongThreadInterprocedural"
        disable += "InvalidPackage"
        baseline = file("lint-baseline.xml")
        htmlReport = true
        sarifReport = true
        textReport = false
        xmlReport = false
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
