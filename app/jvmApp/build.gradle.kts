import com.sorrowblue.comicviewer.configureKotlin
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
    id("comicviewer.primitive.jvm-app-version")
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
            implementation(libs.metro.viewmodelCompose)
        }
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

abstract class CopyConveyorInputsTask @Inject constructor(
    private val fileSystemOperations: FileSystemOperations,
) : DefaultTask() {

    @get:InputFile
    abstract val configFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        val confFile = configFile.get().asFile
        if (!confFile.exists()) {
            throw GradleException("Conveyor config not found: ${confFile.path}")
        }

        val outDir = outputDir.get().asFile
        outDir.deleteRecursively()
        outDir.mkdirs()

        var isTargetSection = false
        val triggerComment = "// Inputs from dependency configurations and the JAR task."

        // 正規表現の解説:
        // 1. (?:"([^"]+)"|([^\s"\[\],]+)) :
        //    - "で囲まれた中身 (グループ1) OR 引用符なしの文字列 (グループ2)
        // 2. (?:\s*->\s*(?:(?:"([^"]+)")|([^\s"\[\],]+)))? :
        //    - オプションで "->" の後に続く 引用符あり (グループ3) OR 引用符なし (グループ4) のエイリアス
        val regex = """(?:"([^"]+)"|([^\s"\[\],]+))(?:\s*->\s*(?:(?:"([^"]+)")|([^\s"\[\],]+)))?""".toRegex()

        confFile.useLines { lines ->
            lines.forEach { line ->
                val trimmed = line.trim()

                // セクションの開始判定
                if (!isTargetSection) {
                    if (trimmed == triggerComment) isTargetSection = true
                    return@forEach
                }

                // セクション内での不要な行（コメント、空行、ブロック記号のみ）をスキップ
                if (trimmed.isEmpty() || trimmed.startsWith("//") || trimmed == "[" || trimmed == "]") {
                    return@forEach
                }

                // app.inputs += などの接頭辞を除去して解析しやすくする
                val cleanLine = trimmed
                    .replaceFirst("app.inputs\\s*\\+?=\\s*\\$\\{app\\.inputs\\}\\s*\\[".toRegex(), "")
                    .replaceFirst("app.inputs\\s*\\+?=\\s*".toRegex(), "")
                    .trim()

                val match = regex.find(cleanLine)
                if (match != null) {
                    // グループ1(引用符あり)かグループ2(引用符なし)からパスを取得
                    val rawSrcPath = match.groups[1]?.value ?: match.groups[2]?.value ?: return@forEach
                    val srcPath = rawSrcPath.replace("\\\\", "\\") // Windowsエスケープ対策

                    // グループ3(引用符ありエイリアス)かグループ4(引用符なしエイリアス)からエイリアス名を取得
                    val aliasName = match.groups[3]?.value ?: match.groups[4]?.value

                    val srcFile = File(srcPath)
                    if (srcFile.exists()) {
                        val targetFileName = aliasName?.trim() ?: srcFile.name
                        logger.lifecycle("Copying: ${srcFile.name} -> $targetFileName")

                        fileSystemOperations.copy {
                            from(srcFile)
                            into(outDir)
                            rename { targetFileName }
                            duplicatesStrategy = DuplicatesStrategy.INCLUDE
                        }
                    } else {
                        // GitHub Actions上では絶対パスが異なる場合があるためのログ
                        logger.warn("File not found: $srcPath")
                    }
                }
            }
        }
        logger.lifecycle("All files copied to: ${outDir.path}")
    }
}

tasks.register<CopyConveyorInputsTask>("copyConveyorInputs") {
    description = "Conveyorのconfigからapp.inputsを解析し、リネームを考慮して特定のディレクトリにコピーします"
    group = "distribution"

    dependsOn("writeConveyorConfig", "jvmJar")

    configFile.set(layout.projectDirectory.file("generated.conveyor.conf"))
    outputDir.set(layout.buildDirectory.dir("all-libs"))
}
