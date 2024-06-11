plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlin.plugin.parcelize)
}

kotlin {
    jvm("desktop")
    androidTarget {
        compilerOptions {
            freeCompilerArgs.addAll("-P", "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.sorrowblue.comicviewer.multi.Parcelize")
        }
    }
    sourceSets {
        val desktopMain by getting
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
        }
        androidMain.dependencies {
            implementation(libs.google.dagger.hilt.android)
        }
        desktopMain.dependencies {
        }
    }
}
android {
    namespace = "com.sorrowblue.comicviewer.domain.multi"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    add("kspAndroid", libs.google.dagger.compiler)
    add("kspAndroid", libs.google.dagger.hilt.compiler)
}
