plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.dynamicfeature)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.storage.client)
            }
        }
        androidMain {
            dependencies {
                implementation(projects.composeApp)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.mupdf)
            }
        }
        desktopMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.pdfbox)
                implementation(fileTree("libs") {
                    include("*.jar")
                })
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader.document"
    packaging {
        jniLibs.useLegacyPackaging = false
    }

    buildFeatures {
        aidl = true
    }
}

dependencies {
    add("kspAndroid", libs.autoservice.ksp)
}
