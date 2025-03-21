plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.dynamicfeature)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.storage.client)
                implementation(projects.data.datastore)
            }
        }

        androidMain {
            dependencies {
                implementation(projects.composeApp)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.artifex.mupdf.fitz)
            }
        }
        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.pdfbox)
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.reader.document"
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    add("kspAndroid", libs.autoservice.ksp)
}
