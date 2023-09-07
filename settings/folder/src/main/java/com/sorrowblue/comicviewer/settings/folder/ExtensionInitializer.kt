package com.sorrowblue.comicviewer.settings.folder

import android.content.Context
import androidx.startup.Initializer
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import com.sorrowblue.comicviewer.framework.LogcatInitializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat

@Suppress("unused")
internal class ExtensionInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (!SplitInstallManagerFactory.create(context).installedModules.contains("document")) {
            // If document is not installed
            runBlocking {
                EntryPointAccessors.fromApplication<InitializerInterface>(context)
                    .folderSettingsUseCase().edit { settings ->
                        val extensions =
                            settings.supportExtension.filterNot { it is SupportExtension.Document }
                                .toSet()
                        logcat(LogPriority.INFO) { "Initialized supportExtension. $extensions" }
                        settings.copy(supportExtension = extensions)
                    }
            }
        }
    }

    override fun dependencies() = listOf(LogcatInitializer::class.java)

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface InitializerInterface {
        fun folderSettingsUseCase(): ManageFolderSettingsUseCase
    }
}
