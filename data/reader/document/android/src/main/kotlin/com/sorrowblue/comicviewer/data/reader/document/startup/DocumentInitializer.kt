package com.sorrowblue.comicviewer.data.reader.document.startup

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.data.reader.document.PdfPluginPackage
import com.sorrowblue.comicviewer.data.reader.document.PdfPluginService
import com.sorrowblue.comicviewer.data.reader.document.ReaderDocumentContext
import com.sorrowblue.comicviewer.domain.model.SupportExtension.Document
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

private const val SupportMajorVersion = 1

internal class DocumentInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        runBlocking {
            runCatching {
                with(context.require<ReaderDocumentContext.Factory>().createReaderDocumentContext()) {
                    updatePdfPluginSupport(context)
                }
            }.onFailure {
                logcat(LogPriority.WARN) { "$PdfPluginPackage: ${it.asLog()}" }
            }
        }
    }

    context(context: ReaderDocumentContext)
    private suspend fun updatePdfPluginSupport(platformContext: PlatformContext) {
        val datastoreDataSource = context.datastoreDataSource
        if (checkPdfService(platformContext)) {
            val versionName =
                platformContext.packageManager.getPackageInfo(PdfPluginPackage, 0).versionName
            val targetMajor = requireNotNull(versionName).split(".")[0].toInt()
            this.logcat(
                LogPriority.INFO,
            ) {
                "PdfPlugin versionName=$versionName, supportMajor=$SupportMajorVersion, targetMajor=$targetMajor"
            }
            if (SupportMajorVersion <= targetMajor) {
                // Supported
                datastoreDataSource.updateFolderSettings { settings ->
                    settings.copy(
                        supportExtension = settings.supportExtension.plus(Document.entries),
                    )
                }
            } else {
                // Not supported
                this.logcat(LogPriority.INFO) { "PdfPlugin is not supported." }
                datastoreDataSource.updateFolderSettings { settings ->
                    settings.copy(
                        supportExtension = settings.supportExtension.minus(Document.entries),
                    )
                }
            }
        } else {
            this.logcat(LogPriority.INFO) { "PdfPlugin is not supported." }
            datastoreDataSource.updateFolderSettings { settings ->
                settings.copy(
                    supportExtension = settings.supportExtension.filterNot {
                        it in
                            Document.entries
                    },
                )
            }
        }
        this.logcat(
            LogPriority.INFO,
        ) { "Initialized document. ${datastoreDataSource.folderSettings.first().supportExtension}" }
    }

    override fun dependencies() = listOf(LogcatInitializer::class.java)

    private fun checkPdfService(context: Context): Boolean {
        val resolveInfos = context.packageManager.queryIntentServices(
            Intent().apply {
                component = ComponentName(PdfPluginPackage, PdfPluginService)
            },
            PackageManager.MATCH_DEFAULT_ONLY,
        )
        return resolveInfos.isNotEmpty()
    }
}
