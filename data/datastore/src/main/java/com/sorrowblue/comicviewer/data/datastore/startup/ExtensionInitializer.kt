package com.sorrowblue.comicviewer.data.datastore.startup

import android.content.Context
import androidx.startup.Initializer
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat

@Suppress("unused")
internal class ExtensionInitializer : Initializer<Unit> {

    @Inject
    lateinit var datastoreDataSource: DatastoreDataSource

    @Inject
    lateinit var splitInstallManager: SplitInstallManager

    override fun create(context: Context) {
        InitializerEntryPoint.resolve(context).inject(this)
        val extensions = if (splitInstallManager.installedModules.contains("document")) {
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

    override fun dependencies() = listOf(LogcatInitializer::class.java)
}
