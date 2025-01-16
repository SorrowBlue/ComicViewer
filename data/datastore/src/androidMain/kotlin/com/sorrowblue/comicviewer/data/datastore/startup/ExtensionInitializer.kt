package com.sorrowblue.comicviewer.data.datastore.startup

import android.content.Context
import androidx.startup.Initializer
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat
import org.koin.androix.startup.KoinInitializer
import org.koin.core.annotation.Singleton
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("unused")
internal class ExtensionInitializer : Initializer<Unit>, KoinComponent {

    private val datastoreDataSource: DatastoreDataSource by inject()

    private val splitInstallManager: SplitInstallManager by inject()

    override fun create(context: Context) {
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

    override fun dependencies() = listOf(LogcatInitializer::class.java, KoinInitializer::class.java)
}

@Singleton
internal fun provideSplitInstallManager(context: Context) =
    SplitInstallManagerFactory.create(context)
