import com.sorrowblue.comicviewer.configureKotlin
import com.sorrowblue.comicviewer.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.comicviewer.primitive.dokka)
    alias(libs.plugins.comicviewer.primitive.detekt)
    alias(libs.plugins.comicviewer.primitive.aboutlibraries)
}

configureKotlin<KotlinMultiplatformExtension>()

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.sorrowblue.comicviewer.app")
            export(projects.data.storage)
            export(projects.data.storage.smb)
            export(projects.data.storage.device)
            export(projects.data.reader.zip)
            export(projects.domain.model)
        }
    }
    sourceSets {
        iosMain.dependencies {
            implementation(projects.app.share)
            implementation(projects.framework.common)
            api(projects.data.storage.smb)
            api(projects.data.storage.device)
            api(projects.data.reader.zip)
            api(projects.domain.model)
            api(libs.metro.viewmodelCompose)
        }
    }
}

aboutLibraries {
    export {
        outputFile.set(rootProject.layout.projectDirectory.file("feature/settings/info/src/iosMain/composeResources/files/aboutlibraries.json"))
    }
}
