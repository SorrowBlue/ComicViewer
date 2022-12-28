package com.sorrowblue.buildlogic

import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
//            with(pluginManager) {
//                apply("com.android.library")
//                apply("org.jetbrains.kotlin.android")
//            }
            useAndroidExtension()
            useAndroidLibraryExtension()
            tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
                kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
            }
        }
    }
}

fun Project.inferNameSpace(s: String): String {
    val a = if (parent?.name == rootProject.name) null else parent?.name
    return if (a == null) {
        "com.sorrowblue.comicviewer.$s"
    } else {
        parent!!.inferNameSpace("$a.$s")
    }
}

fun Project.useAndroidExtension() {
    extensions.configure<BaseExtension> {
        compileSdkVersion(libs.version("android-compile-sdk").toInt())
        buildToolsVersion(libs.version("android-buildtools"))
        namespace = inferNameSpace(name).also {
            logger.lifecycle("nameSpace=$it")
        }
        defaultConfig {
            minSdk = libs.version("android-min-sdk").toInt()
            targetSdk = libs.version("android-target-sdk").toInt()
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

fun Project.useAndroidLibraryExtension() {
    extensions.configure<BaseExtension> {
        buildTypes {
            getByName("release") {
                consumerProguardFiles("consumer-rules.pro")
            }
            getByName("debug") {
                consumerProguardFiles("consumer-rules.pro")
            }
        }
    }
}

fun BaseExtension.kotlinOptions(configure: Action<KotlinJvmOptions>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("kotlinOptions", configure)

val Project.libs: VersionCatalog
    get() {
        return extensions.getByType<VersionCatalogsExtension>().named("libs")
    }

fun VersionCatalog.version(name: String): String {
    return findVersion(name).get().requiredVersion
}
