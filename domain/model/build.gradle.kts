plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sorrowblue.comicviewer.domain.model"
}

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(17)
    }
    androidTarget()
    jvm()
    sourceSets{
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.core)
            }
        }
    }
//         This is for iPhone emulator
//         Switch here to iosArm64 (or iosArm32) to build library for iPhone device, rather than simulator
//        iosX64("ios"){
//            binaries {
//                framework()
//            }
//        }
//    }
//
//    sourceSets {
//        commonMain {
//            dependencies {
//                implementation kotlin('stdlib-common')
//            }
//        }
//        jvmMain {
//            dependencies {
//                implementation kotlin('stdlib')
//            }
//        }
//        jsMain {
//            dependencies {
//                implementation kotlin('stdlib-js')
//            }
//        }
}
// workaround for https://youtrack.jetbrains.com/issue/KT-27170
//configurations {
//    compileClasspath
//}
