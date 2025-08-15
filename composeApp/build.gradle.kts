import com.android.build.api.variant.VariantOutput
import com.sorrowblue.comicviewer.ComicBuildType
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.application)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.sorrowblue.comicviewer.app")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.framework.designsystem)
            implementation(projects.framework.ui)
            implementation(projects.data.di)
            implementation(projects.domain.usecase)
            implementation(projects.feature.authentication)
            implementation(projects.feature.bookshelf)
            implementation(projects.feature.bookshelf.info)
            implementation(projects.feature.book)
            implementation(projects.feature.readlater)
            implementation(projects.feature.collection)
            implementation(projects.feature.search)
            implementation(projects.feature.tutorial)
            implementation(projects.feature.folder)
            implementation(projects.feature.settings)
            implementation(projects.feature.settings.info)
            implementation(projects.feature.history)

            // Di
            implementation(libs.koin.composeViewModel)

            implementation(libs.kotlinx.serialization.json)
        }


        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.kotlin.test)
                implementation(libs.koin.test)
            }
        }

        androidMain.dependencies {
            implementation(projects.framework.notification)

            implementation(libs.androidx.core.splashscreen)
            implementation(libs.koin.androidxCompose)
            implementation(libs.koin.androidxStartup)
            implementation(libs.koin.androidxWorkmanager)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.google.android.play.feature.delivery.ktx)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        androidUnitTest {
            dependencies {
                implementation(projects.data.storage.client)
            }
        }

        desktopTest {
            dependencies {
                implementation(projects.data.storage.client)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.app"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
    }
    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }

    buildTypes {
        val release by getting {
            applicationIdSuffix = ComicBuildType.RELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.RELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.RELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        prerelease {
            initWith(release)
            applicationIdSuffix = ComicBuildType.PRERELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.PRERELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.PRERELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        internal {
            initWith(release)
            applicationIdSuffix = ComicBuildType.INTERNAL.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.INTERNAL.isMinifyEnabled
            isShrinkResources = ComicBuildType.INTERNAL.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        debug {
            applicationIdSuffix = ComicBuildType.DEBUG.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.DEBUG.isMinifyEnabled
            isShrinkResources = ComicBuildType.DEBUG.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
    }

    buildFeatures.buildConfig = true

    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }

    sourceSets {
        getByName("main").res.srcDirs("src/commonMain/composeResources")
    }

    lint {
        val androidLintCheckReleaseBuilds: Boolean? by project
        checkReleaseBuilds = androidLintCheckReleaseBuilds ?: true
        abortOnError = true
    }
}

compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.app.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.sorrowblue.comicviewer.app"
            packageVersion = "1.0.0"
        }
        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    }
}

val gitTagProvider = providers.of(GitTagValueSource::class) {}

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.outputs.forEach { output: VariantOutput ->
            val vn = gitTagProvider.orElse("0.0.0").get()
            output.versionName.set(vn)
            logger.lifecycle("${variant.name} versionName=$vn")
        }
    }
}
