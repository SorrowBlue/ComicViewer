import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.extractPackageVersion
import com.sorrowblue.comicviewer.gitTagProvider
import com.sorrowblue.comicviewer.libs
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.comicviewer.primitive.detekt)
    alias(libs.plugins.comicviewer.primitive.dokka)
    alias(libs.plugins.comicviewer.primitive.aboutlibraries)
    id("dev.hydraulic.conveyor") version "2.0"
}

aboutLibraries {
    export {
        outputFile.set(file("src/jvmMain/composeResources/files/aboutlibraries.json"))
    }
}
configureKotlin<KotlinMultiplatformExtension>()

kotlin {
    jvm()
    applyDefaultHierarchyTemplate()
    sourceSets {
        jvmMain.dependencies {
            implementation(projects.app.share)
            implementation(projects.feature.settings.info)
            implementation(projects.framework.common)
            implementation(projects.framework.designsystem)
            implementation(projects.framework.ui)

            implementation(compose.desktop.currentOs)

            implementation(libs.androidx.lifecycleViewmodelCompose)
            implementation(libs.compose.material3)
            implementation(libs.filekit.core)
            implementation(libs.jcifs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.compose.componentsAnimatedimage)
            implementation(libs.metro.viewmodelCompose)
        }
    }
}

val gitTagProvider = providers.of(GitTagValueSource::class) {}
version = extractPackageVersion(gitTagProvider.orElse("1.0.0").get())

compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.app.MainKt"
        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Exe,
                TargetFormat.Deb,
            )

            packageName = "com-sorrowblue-comicviewer"
            packageVersion = extractPackageVersion(gitTagProvider.orElse("1.0.0").get())
            vendor = "SorrowBlue"
            description = "Multi-platform Comic Viewer"
            copyright = "Copyright 2026 SorrowBlue."
            licenseFile.set(rootProject.file("LICENSE"))

            linux {
                debMaintainer = "sorrowblue.dev@gmail.com"
                menuGroup = "comicviewer"
                appCategory = "Utility;Viewer;"
            }

            macOS {
                bundleID = "com.sorrowblue.comicviewer"
                appCategory = "public.app-category.books"
                dockName = "ComicViewer"
                infoPlist {
                    extraKeysRawXml = """
                        <key>NSHighResolutionCapable</key>
                        <true/>
                    """.trimIndent()
                }
            }
            windows {
                installationPath = "ComicViewer"
                dirChooser = true
                menuGroup = "ComicViewer"
                upgradeUuid = "F5DB26A2-175B-446C-9EDA-50ACACCB6F8C"
                shortcut = true
                perUserInstall = false
                console = false
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
            }
        }
        jvmArgs(
            "-Dsun.stdout.encoding=UTF-8",
            "-Dsun.stderr.encoding=UTF-8",
        )
    }
}
