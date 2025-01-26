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
//                implementation(projects.app)
                implementation(projects.composeApp)
                implementation(libs.androidx.core.ktx)
                implementation(libs.artifex.mupdf.fitz)
                implementation(libs.google.autoServiceAnnotations)
            }
        }
        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
            implementation("org.apache.pdfbox:pdfbox:3.0.4")
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
