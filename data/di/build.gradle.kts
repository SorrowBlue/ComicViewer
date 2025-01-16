plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.domain.service)
                implementation(projects.data.coil)
                implementation(projects.data.database)
                implementation(projects.data.datastore)
                implementation(projects.data.reader.zip)
                implementation(projects.data.storage.client)
                implementation(projects.data.storage.device)
                implementation(projects.data.storage.smb)
            }
        }

        androidMain {
            dependencies {
                // :feature:library:box :feature:library:onedrive
                implementation(libs.squareup.okhttp3)
                implementation(libs.bouncycastle.bcprovJdk18on)
                // :feature:library:dropbox :feature:library:onedrive
                implementation(libs.fasterxml.jackson.core)
                // :feature:library:googledrive :feature:library:onedrive
                implementation(libs.google.code.gson)
                implementation(libs.androidx.credentials.playServicesAuth)
                // :feature:library:googledrive
                // Type com.google.common.util.concurrent.ListenableFuture is defined multiple times:
                implementation(libs.google.guava)


                val dh = this
                val skipModule = listOf(
                    projects.app,
                    projects.catalog,
                    projects.composeApp,
                    projects.data.di,
                    projects.data.reader.document,
                    projects.feature.library.box,
                    projects.feature.library.dropbox,
                    projects.feature.library.googledrive,
                    projects.feature.library.onedrive,
                ).map { it.test() }
                logger.lifecycle("projects.feature.history.group: ${projects.feature.history.test()}")
                rootProject.subprojects {
                    if (!this.project.isModuleEmpty() && !skipModule.contains(this.project.parentName())) {
                        logger.lifecycle("${this.project.parentName()}")
                        dh.implementation(this.project)
                    }
                }
            }
        }
    }
}

fun ProjectDependency.test() = ":" + group.toString().split(".").drop(1).joinToString(":") + ":$name"

internal fun Project.parentName(): String = ":" + group.toString().split(".").drop(1).joinToString(":") + ":$name"

fun Project.isModuleEmpty(): Boolean {
    val buildFile = File(projectDir, "build.gradle.kts")
    return if (!buildFile.exists()) {
        true
    } else {
        val lines = buildFile.readLines()
        lines.filter{ it.isNotBlank() }.size <= 2 // 空白行を除き2行以下なら空
    }
}
android {
    namespace = "com.sorrowblue.comicviewer.data.di"
}
