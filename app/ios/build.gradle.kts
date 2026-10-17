import com.sorrowblue.comicviewer.diSubprojects

plugins {
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.comicviewer.primitive.dokka)
    alias(libs.plugins.comicviewer.primitive.detekt)
    alias(libs.plugins.comicviewer.primitive.aboutlibraries)
}

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
            diSubprojects.forEach(::implementation)
            api(projects.data.storage)
            api(projects.data.storage.smb)
            api(projects.data.storage.device)
            api(projects.data.reader.zip)
            api(projects.domain.model)
        }
    }
}

aboutLibraries {
    export {
        outputFile.set(rootProject.layout.projectDirectory.file("feature/settings/info/src/iosMain/composeResources/files/aboutlibraries.json"))
    }
}
