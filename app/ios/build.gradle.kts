import com.sorrowblue.comicviewer.diSubprojects

plugins {
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.comicviewer.primitive.dokka)
    alias(libs.plugins.comicviewer.primitive.detekt)
    alias(libs.plugins.comicviewer.primitive.aboutlibraries)
}

kotlin {
    val exportedProjects = listOf(
        projects.data.storage,
        projects.data.storage.smb,
        projects.data.storage.device,
        projects.data.reader.zip,
        projects.domain.model,
    )
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.sorrowblue.comicviewer.app")
            exportedProjects.forEach(::export)
        }
    }
    sourceSets {
        iosMain.dependencies {
            val exportedPaths = exportedProjects.map { it.path }.toSet()
            diSubprojects.filterNot { it.path in exportedPaths }.forEach(::implementation)
            exportedProjects.forEach(::api)
        }
    }
}

aboutLibraries {
    export {
        outputFile.set(rootProject.layout.projectDirectory.file("feature/settings/info/src/iosMain/composeResources/files/aboutlibraries.json"))
    }
}
