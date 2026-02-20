package com.sorrowblue.comicviewer.data.reader.document.startup

import com.sorrowblue.comicviewer.data.reader.document.SupportedMajorVersion
import com.sorrowblue.comicviewer.domain.model.SupportExtension.Document
import com.sorrowblue.comicviewer.domain.service.datasource.DocumentReaderState
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.starup.LogcatInitializer
import dev.zacsweers.metro.Inject
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat

@Inject
internal class DocumentInitializer(private val platformContext: PlatformContext) :
    Initializer<Unit> {
    override fun create() {
        platformContext.withDocumentInitializerContext {
            val state =
                pdfPluginDataSourceFactory.initializePdfPlugin(
                    runBlocking { datastoreDataSource.pdfPluginSettings.first() }.pluginRootPath,
                )
            when (state) {
                DocumentReaderState.Success -> {
                    // Version OK
                    updateSupportExtension(true)
                    updatePdfPluginState(true)
                    logcat(LogPriority.INFO) { "Initialized document-reader." }
                }

                is DocumentReaderState.InvalidVersion -> {
                    // Version NG
                    updateSupportExtension(false)
                    updatePdfPluginState(false)
                    logcat(
                        LogPriority.INFO,
                    ) {
                        "Initialized document-reader. Plugin is old. version=${state.version}, supportedMajor=$SupportedMajorVersion"
                    }
                }

                DocumentReaderState.Error -> {
                    // PDFプラグイン読み込みエラー
                    updateSupportExtension(false)
                    updatePdfPluginState(false)
                    logcat { "Initialized document-reader. Plugin load failed." }
                }

                DocumentReaderState.NotFound -> {
                    // プラグインが見つからなかった
                    updateSupportExtension(false)
                    updatePdfPluginState(false)
                    logcat {
                        "Initialized document-reader. Plugin not found. path=${runBlocking {
                            datastoreDataSource.pdfPluginSettings.first().pluginRootPath
                        }}"
                    }
                }
            }
        }
    }

    override fun dependencies(): List<KClass<out Initializer<*>>?> =
        listOf(LogcatInitializer::class)

    context(context: DocumentInitializerContext)
    private fun updateSupportExtension(isSupportDocument: Boolean) {
        runBlocking {
            context.datastoreDataSource.updateFolderSettings { settings ->
                val supportedException = if (isSupportDocument) {
                    settings.supportExtension.plus(Document.entries)
                } else {
                    settings.supportExtension.minus(Document.entries)
                }
                settings.copy(supportExtension = supportedException)
            }
        }
    }

    context(context: DocumentInitializerContext)
    private fun updatePdfPluginState(isEnabled: Boolean) {
        runBlocking {
            context.datastoreDataSource.updatePdfPluginSettings { settings ->
                settings.copy(isEnabled = isEnabled)
            }
        }
    }
}
