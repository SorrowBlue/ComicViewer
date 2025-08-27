package com.sorrowblue.comicviewer.data.storage.client.startup

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import org.koin.androix.startup.KoinInitializer
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class ExtensionInitializer : Initializer<Unit>, KoinComponent {

    private val datastoreDataSource: DatastoreDataSource by inject()
    private val pkg = "com.sorrowblue.comicviewer.plugin.pdf"

    override fun create(context: Context) {
        val supportMajor = 0
        runCatching {
            val versionName = context.packageManager.getPackageInfo(pkg, 0).versionName
            logcat(LogPriority.INFO) { "$pkg: versionName: $versionName, supportMajor: $supportMajor" }
            val major = versionName!!.split(".")[0].toInt()
            if (supportMajor <= major) {
                // Supported
                val extensions = if (checkPdfService(context)) {
                    SupportExtension.Archive.entries + SupportExtension.Document.entries
                } else {
                    SupportExtension.Archive.entries
                }
                runBlocking {
                    datastoreDataSource.updateFolderSettings { settings ->
                        settings.copy(supportExtension = extensions)
                    }
                }
                logcat(LogPriority.INFO) { "Initialized supportExtension. $extensions" }
            } else {
                // Not supported
                logcat(LogPriority.WARN) { "$pkg is not supported." }
            }
        }.onFailure {
            logcat(LogPriority.WARN) { "$pkg: ${it.asLog()}" }
        }
    }

    @OptIn(KoinExperimentalAPI::class)
    override fun dependencies() = listOf(LogcatInitializer::class.java, KoinInitializer::class.java)

    private fun checkPdfService(context: Context): Boolean {
        val resolveInfos = context.packageManager.queryIntentServices(
            Intent().apply {
                component = ComponentName(
                    pkg,
                    "com.sorrowblue.comicviewer.plugin.pdf.PdfService"
                )
            },
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveInfos.isNotEmpty()
    }
}
