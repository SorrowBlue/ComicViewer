package comicviewer.convention

import com.sorrowblue.comicviewer.configureKotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

plugins {
    com.android.library
    id("comicviewer.primitive.lint")
    id("comicviewer.primitive.detekt")
    id("comicviewer.primitive.dokka")
    id("comicviewer.primitive.aboutlibraries")
}

kotlin {
    configureKotlin<KotlinAndroidProjectExtension>()
    compilerOptions {
        if (project.path.startsWith(":data")) {
            optIn.add("com.sorrowblue.comicviewer.domain.model.InternalDataApi")
        }
    }
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":framework:common"))
}
