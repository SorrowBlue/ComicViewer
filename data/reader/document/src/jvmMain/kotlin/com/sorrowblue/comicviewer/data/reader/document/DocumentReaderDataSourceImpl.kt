package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.domain.service.datasource.DocumentReaderDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DocumentReaderState
import com.sorrowblue.comicviewer.plugin.pdf.PdfPlugin
import java.io.File
import java.net.URLClassLoader
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

internal class DocumentReaderDataSourceImpl : DocumentReaderDataSource {
    private var jarPaths: List<String> = emptyList()

    override val version: String get() = OutsideDocumentFileReader.plugin.version

    override fun initializePdfPlugin(rootPath: String): DocumentReaderState {
        val jarPaths = findPluginJarPaths(rootPath)
        if (jarPaths.isNotEmpty() && this.jarPaths == jarPaths) {
            // 同じパス
            return DocumentReaderState.Success
        } else if (jarPaths.isNotEmpty()) {
            val plugin = getPlugin(jarPaths)
            if (plugin != null) {
                if (checkVersion(plugin)) {
                    // Version OK
                    runCatching {
                        plugin.init()
                    }.onFailure {
                        return DocumentReaderState.Error
                    }

                    this.jarPaths = jarPaths
                    OutsideDocumentFileReader.plugin = plugin
                    // 拡張子更新
                    return DocumentReaderState.Success
                } else {
                    // Version NG
                    return DocumentReaderState.InvalidVersion(plugin.version)
                }
            } else {
                // PDFプラグイン読み込みエラー
                return DocumentReaderState.Error
            }
        } else {
            // プラグインが見つからなかった
            return DocumentReaderState.NotFound
        }
    }

    /**
     * プラグインのjarファイルパスを見つける。
     * プラグインの有効性は確認しません。
     *
     * @return プラグインのjarファイルパス。見つからなかった場合は空のリスト。
     */
    private fun findPluginJarPaths(rootPath: String): List<String> {
        val libFiles = File(rootPath).resolve("app").listFiles {
            it.isFile && it.extension == "jar" && it.name.containsStartsWith(
                PREFIX_LIB_JVM,
                PREFIX_PDF_DESKTOP,
            )
        }
        if (libFiles?.count() == 2) {
            var libJvmPath: String? = null
            var pdfDesktopPath: String? = null
            libFiles.forEach {
                if (it.name.startsWith(PREFIX_LIB_JVM)) {
                    libJvmPath = it.path
                } else if (it.name.startsWith(PREFIX_PDF_DESKTOP)) {
                    pdfDesktopPath = it.path
                }
            }
            if (libJvmPath != null && pdfDesktopPath != null) {
                return listOf(libJvmPath, pdfDesktopPath)
            }
        }
        return emptyList()
    }

    private fun getPlugin(jarPaths: List<String>): PdfPlugin? = runCatching {
        jarPaths.map { File(it).toURI().toURL() }
            .toTypedArray()
            .let { URLClassLoader(it) }
            .loadClass(PdfPluginImpl)
            .getDeclaredConstructor()
            .newInstance() as PdfPlugin
    }.onSuccess {
        logcat(
            "PdfPluginWrapper",
            LogPriority.INFO,
        ) { "PDF-plugin initialized. version=${it.version}, ${it.timestamp}" }
    }.onFailure {
        logcat(
            "PdfPluginWrapper",
            LogPriority.ERROR,
        ) { "PDF-plugin failed to initialize. \n${it.asLog()}" }
    }.getOrNull()

    private fun checkVersion(plugin: PdfPlugin): Boolean {
        val version = plugin.version.split(".").firstOrNull()?.toIntOrNull() ?: 0
        return SupportedMajorVersion <= version
    }

    private fun String.containsStartsWith(vararg prefix: String): Boolean = prefix.any {
        startsWith(it)
    }
}

private const val PREFIX_LIB_JVM = "lib-jvm-"
private const val PREFIX_PDF_DESKTOP = "pdf-desktop-"

private const val PdfPluginImpl = "com.sorrowblue.comicviewer.plugin.pdf.PdfPluginImpl"

const val SupportedMajorVersion = 1
