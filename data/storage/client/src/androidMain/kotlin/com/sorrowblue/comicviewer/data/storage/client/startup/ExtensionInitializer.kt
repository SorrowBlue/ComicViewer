package com.sorrowblue.comicviewer.data.storage.client.startup

import android.content.Context
import androidx.startup.Initializer
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.sorrowblue.comicviewer.data.storage.client.DynamicFeatureModuleProvider
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import java.util.ServiceLoader
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat
import org.koin.androix.startup.KoinInitializer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules

@Suppress("unused")
internal class ExtensionInitializer : Initializer<Unit>, KoinComponent {

    private val datastoreDataSource: DatastoreDataSource by inject()

    override fun create(context: Context) {
        val splitInstallManager = SplitInstallManagerFactory.create(context)
        val extensions = if (splitInstallManager.installedModules.contains("document")) {
            val serviceLoader = ServiceLoader.load(
                DynamicFeatureModuleProvider::class.java,
                DynamicFeatureModuleProvider::class.java.classLoader
            )
            loadKoinModules(serviceLoader.iterator().next().module())
            logcat(LogPriority.INFO) { "loadKoinModules DynamicFeatureModuleProvider" }
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
