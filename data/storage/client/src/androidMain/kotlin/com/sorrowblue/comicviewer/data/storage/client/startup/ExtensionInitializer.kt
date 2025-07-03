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
import logcat.logcat
import org.koin.androix.startup.KoinInitializer
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class ExtensionInitializer : Initializer<Unit>, KoinComponent {

    private val datastoreDataSource: DatastoreDataSource by inject()

    override fun create(context: Context) {
        val resolveInfos = context.packageManager.queryIntentServices(
            Intent().apply {
                component = ComponentName(
                    "com.sorrowblue.comicviewer.pdf",
                    "com.sorrowblue.comicviewer.pdf.PdfService"
                )
            },
            PackageManager.MATCH_DEFAULT_ONLY
        )
        logcat(LogPriority.INFO) { "resolveInfos: ${resolveInfos.isNotEmpty()}" }
        val extensions = if (resolveInfos.isNotEmpty()) {
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
    }

    @OptIn(KoinExperimentalAPI::class)
    override fun dependencies() = listOf(LogcatInitializer::class.java, KoinInitializer::class.java)
}
