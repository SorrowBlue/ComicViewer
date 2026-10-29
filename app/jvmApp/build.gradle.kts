import com.sorrowblue.comicviewer.diSubprojects
import java.util.zip.ZipFile
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.comicviewer.multiplatformCompose)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.comicviewer.di)
    alias(libs.plugins.comicviewer.primitive.detekt)
    alias(libs.plugins.comicviewer.primitive.dokka)
    alias(libs.plugins.comicviewer.primitive.aboutlibraries)
    id("dev.hydraulic.conveyor") version "2.0"
    id("comicviewer.primitive.jvm-app-version")
}

kotlin {
    jvm()
    applyDefaultHierarchyTemplate()
    sourceSets {
        jvmMain.dependencies {
            diSubprojects.forEach(::implementation)

            implementation(compose.desktop.currentOs)

            implementation(libs.compose.ui)

            runtimeOnly(libs.kotlinx.coroutinesSwing)
        }
    }
}

metro {
    @Suppress("OPT_IN_USAGE")
    hiddenDependencies {
        checkOnCompile.set(true)
    }
}

aboutLibraries {
    export {
        outputFile.set(rootProject.layout.projectDirectory.file("feature/settings/info/src/jvmMain/composeResources/files/aboutlibraries.json"))
    }
}

compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.MainKt"
        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Exe,
                TargetFormat.Deb,
            )

            packageName = "com-sorrowblue-comicviewer"
            packageVersion = version.toString()
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
            "-Dfile.encoding=UTF-8",
            "-Dsun.stdout.encoding=UTF-8",
            "-Dsun.stderr.encoding=UTF-8",
        )
    }
}
tasks.withType<JavaExec> {
    jvmArgs("-Dfile.encoding=UTF-8", "-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
}

abstract class InspectConveyorDependenciesTask : DefaultTask() {

    @get:InputFiles
    @get:Classpath
    abstract val runtimeJars: ConfigurableFileCollection

    @get:Input
    abstract val jarPathToCoordinate: MapProperty<String, String>

    @get:OutputFile
    abstract val manifestOutputFile: RegularFileProperty

    @get:OutputFile
    abstract val errorsOutputFile: RegularFileProperty

    @TaskAction
    fun run() {
        val osNameRegex = Regex(
            "(?:^|[-_.])(windows|win32|win64|linux|macos|osx|darwin)(?:[-_.]|$)",
            RegexOption.IGNORE_CASE,
        )
        val versionedSoRegex = Regex("""\.so(\.\d+)+$""", RegexOption.IGNORE_CASE)

        val dependencies = sortedSetOf<String>()
        val nativeErrors = sortedSetOf<String>()

        jarPathToCoordinate.get().forEach { (jarPath, coord) ->
            dependencies.add(coord)
            val artifact = coord.split(":").getOrElse(1) { File(jarPath).name }

            if (osNameRegex.containsMatchIn(artifact)) {
                nativeErrors.add(
                    "[OS-Specific Artifact Name] $coord (matched OS token in artifact name)",
                )
            }

            val jarFile = File(jarPath)
            if (!jarFile.exists()) {
                nativeErrors.add("[JAR File Not Found] $coord ($jarPath)")
                return@forEach
            }

            runCatching {
                ZipFile(jarFile).use { zip ->
                    val entryNames = zip.entries().asSequence()
                        .filterNot { it.isDirectory }
                        .map { it.name.lowercase() }
                        .toList()

                    val dllCount = entryNames.count { it.endsWith(".dll") }
                    val soCount = entryNames.count {
                        it.endsWith(".so") || versionedSoRegex.containsMatchIn(it)
                    }
                    val dylibCount = entryNames.count {
                        it.endsWith(".dylib") || it.endsWith(".jnilib")
                    }

                    val hasWin = dllCount > 0
                    val hasLinux = soCount > 0
                    val hasMac = dylibCount > 0

                    if ((hasWin || hasLinux || hasMac) && !(hasWin && hasLinux && hasMac)) {
                        val missing = buildList {
                            if (!hasWin) add("Windows(.dll)")
                            if (!hasLinux) add("Linux(.so)")
                            if (!hasMac) add("macOS(.dylib/.jnilib)")
                        }
                        nativeErrors.add(
                            "[Missing Target OS Native Binary] $coord " +
                                "(missing: ${missing.joinToString(", ")} | " +
                                "found: dll=$dllCount, so=$soCount, dylib/jnilib=$dylibCount)",
                        )
                    }
                }
            }.onFailure { e ->
                nativeErrors.add("[JAR Inspection Error] $coord: ${e.message}")
            }
        }

        val manifestFile = manifestOutputFile.get().asFile
        manifestFile.parentFile.mkdirs()
        manifestFile.writeText(
            if (dependencies.isEmpty()) "" else dependencies.joinToString("\n", postfix = "\n"),
        )
        logger.lifecycle("Successfully extracted ${dependencies.size} dependencies to ${manifestFile.path}")

        val errorsFile = errorsOutputFile.get().asFile
        errorsFile.parentFile.mkdirs()
        errorsFile.writeText(
            if (nativeErrors.isEmpty()) "" else nativeErrors.joinToString("\n", postfix = "\n"),
        )
        logger.lifecycle("Native binary inspection complete: ${nativeErrors.size} issue(s) written to ${errorsFile.path}")
    }
}

val jvmRuntimeClasspath = configurations.named("jvmRuntimeClasspath")

tasks.register<InspectConveyorDependenciesTask>("inspectConveyorDependencies") {
    description = "Extracts external JVM runtime dependencies and inspects JARs for OS-specific native binary drift."
    group = "verification"

    val externalArtifacts = jvmRuntimeClasspath.map { config ->
        config.incoming.artifactView {
            componentFilter { it is ModuleComponentIdentifier }
        }.artifacts
    }

    runtimeJars.from(externalArtifacts.map { it.artifactFiles })
    jarPathToCoordinate.set(
        externalArtifacts.map { artifacts ->
            artifacts.resolvedArtifacts.get().mapNotNull { artifact ->
                val id = artifact.id.componentIdentifier as? ModuleComponentIdentifier
                    ?: return@mapNotNull null
                artifact.file.absolutePath to "${id.group}:${id.module}:${id.version}"
            }.toMap()
        },
    )
    manifestOutputFile.set(layout.buildDirectory.file("conveyor/deps.txt"))
    errorsOutputFile.set(layout.buildDirectory.file("conveyor/native-errors.txt"))
}
