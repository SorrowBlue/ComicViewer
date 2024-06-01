package com.sorrowblue.comicviewer.feature.settings.folder

import android.content.Context
import androidx.startup.Initializer
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat

@Suppress("unused")
internal class ExtensionInitializer : Initializer<Unit> {

    @Inject
    lateinit var manageFolderSettingsUseCase: ManageFolderSettingsUseCase

    override fun create(context: Context) {
        InitializerEntryPoint.resolve(context).inject(this)
        if (!SplitInstallManagerFactory.create(context).installedModules.contains("document")) {
            // If document is not installed
            runBlocking {
                manageFolderSettingsUseCase.edit { settings ->
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
}
