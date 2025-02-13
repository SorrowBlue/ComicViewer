plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    listOf(
        iosSimulatorArm64(),
        iosArm64(),
        iosX64()
    ).forEach {
//        it.compilations["main"].cinterops {
//            create("sevenzip") {
//                header(file("src/nativeInterop/cpp/jbinding-cpp/JavaToCPP/JavaToCPPSevenZip.cpp"))
//                defFile("src/nativeInterop/cinterop/sevenzip.def")
//                packageName("sevenzip")
//                 Options to be passed to compiler by cinterop tool.
//                compilerOpts("-I/Users/sorrowblue/Downloads/lzma2409/C")
//            }
//        }
    }
    sourceSets.commonMain.dependencies {
        implementation(projects.data.storage.client)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.io)
    }

    sourceSets.androidMain.dependencies {
        implementation(libs.androidx.startup.runtime)
        implementation(libs.com.sorrowblue.sevenzipjbinding)
    }

    sourceSets.desktopMain.dependencies {
        implementation("net.sf.sevenzipjbinding:sevenzipjbinding:16.02-2.01")
        implementation("net.sf.sevenzipjbinding:sevenzipjbinding-all-platforms:16.02-2.01")
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader.zip"
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}
