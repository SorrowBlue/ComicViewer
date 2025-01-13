import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.dagger.hilt)
    alias(libs.plugins.google.ksp)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.framework.common)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.material3AdaptiveNavigationSuite)
                implementation(compose.materialIconsExtended)
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(projects.domain.usecase)
            }
        }

        androidMain {
            dependencies {

                implementation(projects.framework.ui)
                implementation(projects.framework.notification)
                implementation(projects.framework.designsystem)

                implementation(projects.data.di)
                implementation(projects.domain.usecase)
                implementation(projects.feature.authentication)
                implementation(projects.feature.book)
                implementation(projects.feature.bookshelf)
                implementation(projects.feature.favorite)
                implementation(projects.feature.folder)
                implementation(projects.feature.favorite.add)
                implementation(projects.feature.favorite.create)
                implementation(projects.feature.readlater)
                implementation(projects.feature.search)
                implementation(projects.feature.settings)
                implementation(projects.feature.settings.security)
                implementation(projects.feature.tutorial)
                implementation(projects.feature.library)
                implementation(compose.preview)
                implementation(libs.google.dagger.hilt.android)
                implementation(libs.androidx.hilt.navigationCompose)


                implementation(libs.androidx.activity)
                implementation(libs.androidx.biometric)
                implementation(libs.androidx.browser)
                implementation(libs.androidx.core.splashscreen)
                implementation(libs.androidx.hilt.work)
                implementation(libs.google.android.play.review.ktx)
                implementation(libs.google.android.play.feature.delivery.ktx)
                implementation(libs.androidx.appcompat)

                implementation(libs.google.android.billingclient.billingKtx)
            }
        }

        val koinMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.compose)
            }
        }
        iosMain {
            dependsOn(koinMain)
        }
        val desktopMain by getting {
            dependsOn(koinMain)
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.app"

    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }
    buildTypes {
        getByName("release") {
        }
        all {
            applicationIdSuffix = ".kmp"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspAndroid", libs.google.dagger.hilt.compiler)
}

compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.sorrowblue.comicviewer"
            packageVersion = "1.0.0"
        }
    }
}
