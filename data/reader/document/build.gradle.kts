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
                implementation(projects.app)
                implementation(projects.data.storage.client)

                implementation(libs.androidx.core.ktx)
                implementation(libs.artifex.mupdf.fitz)

                implementation(libs.google.autoServiceAnnotations)
            }
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
