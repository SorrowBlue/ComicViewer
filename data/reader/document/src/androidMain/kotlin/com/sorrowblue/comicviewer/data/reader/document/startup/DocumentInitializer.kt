package com.sorrowblue.comicviewer.data.reader.document.startup

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.data.reader.document.PDF_PLUGIN_PACKAGE
import com.sorrowblue.comicviewer.data.reader.document.PDF_PLUGIN_SERVICE
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import org.koin.androix.startup.KoinInitializer
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val supportMajor = 1

internal class DocumentInitializer : Initializer<Unit>, KoinComponent {

    private val datastoreDataSource: DatastoreDataSource by inject()

    override fun create(context: Context) {
        runBlocking {
            runCatching {
                if (checkPdfService(context)) {
                    val versionName =
                        context.packageManager.getPackageInfo(PDF_PLUGIN_PACKAGE, 0).versionName
                    logcat(LogPriority.INFO) { "PdfPlugin versionName=$versionName, supportMajor=$supportMajor" }
                    val targetMajor = versionName!!.split(".")[0].toInt()
                    if (supportMajor <= targetMajor) {
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
                            settings.copy(supportExtension = settings.supportExtension.filterNot { it in SupportExtension.Document.entries })
                        }
                    }
                } else {
                    logcat(LogPriority.INFO) { "PdfPlugin is not supported." }
                    datastoreDataSource.updateFolderSettings { settings ->
                        settings.copy(supportExtension = settings.supportExtension.filterNot { it in SupportExtension.Document.entries })
                    }
                }
            }.onFailure {
                logcat(LogPriority.WARN) { "$PDF_PLUGIN_PACKAGE: ${it.asLog()}" }
            }
            logcat(LogPriority.INFO) { "Initialized document. ${datastoreDataSource.folderSettings.first().supportExtension}" }
        }
    }

    @OptIn(KoinExperimentalAPI::class)
    override fun dependencies() = listOf(LogcatInitializer::class.java, KoinInitializer::class.java)

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
