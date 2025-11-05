package com.sorrowblue.comicviewer.data.reader.document.startup

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.data.reader.document.PDF_PLUGIN_PACKAGE
import com.sorrowblue.comicviewer.data.reader.document.PDF_PLUGIN_SERVICE
import com.sorrowblue.comicviewer.data.reader.document.di.ReaderDocumentContext
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.platformGraph
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

private const val SUPPORT_MAJOR = 1

internal class DocumentInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        runBlocking {
            runCatching {
                with(context.platformGraph as ReaderDocumentContext) {
                    updatePdfPluginSupport(context)
                }
            }.onFailure {
                logcat(LogPriority.WARN) { "$PDF_PLUGIN_PACKAGE: ${it.asLog()}" }
            }
        }
    }

    context(context: ReaderDocumentContext)
    private suspend fun updatePdfPluginSupport(platformContext: PlatformContext) {
        val datastoreDataSource = context.datastoreDataSource
        if (checkPdfService(platformContext)) {
            val versionName =
                platformContext.packageManager.getPackageInfo(PDF_PLUGIN_PACKAGE, 0).versionName
            val targetMajor = versionName!!.split(".")[0].toInt()
            logcat(LogPriority.INFO) { "PdfPlugin versionName=$versionName, supportMajor=$SUPPORT_MAJOR, targetMajor=$targetMajor" }
            if (SUPPORT_MAJOR <= targetMajor) {
                // Supported
                datastoreDataSource.updateFolderSettings { settings ->
                    settings.copy(
                        supportExtension = settings.supportExtension.toMutableSet()
                            .apply { addAll(SupportExtension.Document.entries) }
                            .toList()
                    )
                }
            } else {
                // Not supported
                logcat(LogPriority.INFO) { "PdfPlugin is not supported." }
                datastoreDataSource.updateFolderSettings { settings ->
                    settings.copy(
                        supportExtension = settings.supportExtension.filterNot { it in SupportExtension.Document.entries }
                    )
                }
            }
        } else {
            logcat(LogPriority.INFO) { "PdfPlugin is not supported." }
            datastoreDataSource.updateFolderSettings { settings ->
                settings.copy(
                    supportExtension = settings.supportExtension.filterNot { it in SupportExtension.Document.entries }
                )
            }
        }
        logcat(
            LogPriority.INFO
        ) { "Initialized document. ${datastoreDataSource.folderSettings.first().supportExtension}" }
    }

    override fun dependencies() = listOf(LogcatInitializer::class.java)

    private fun checkPdfService(context: Context): Boolean {
        val resolveInfos = context.packageManager.queryIntentServices(
            Intent().apply {
                component = ComponentName(PDF_PLUGIN_PACKAGE, PDF_PLUGIN_SERVICE)
            },
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveInfos.isNotEmpty()
    }
}
