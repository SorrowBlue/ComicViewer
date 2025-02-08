plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {

                val dh = this
                val skipModule = listOf(
                    projects.composeApp,
                    projects.data.di,
                    projects.data.reader.document,
                    projects.framework.notification,
                    projects.framework.navigation.kspCompiler,
                ).map { it.test() }
                rootProject.subprojects {
                    if (!this.project.isModuleEmpty() && !skipModule.contains(this.project.parentName())) {
                        dh.implementation(this.project)
                    }
                }
            }
        }

        androidMain {
            dependencies {
                // :feature:library:box :feature:library:onedrive
                // :feature:library:googledrive :feature:library:onedrive
                // :feature:library:googledrive
                // Type com.google.common.util.concurrent.ListenableFuture is defined multiple times:
            }
        }

        desktopMain.dependencies {
            implementation(projects.data.reader.document)
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
