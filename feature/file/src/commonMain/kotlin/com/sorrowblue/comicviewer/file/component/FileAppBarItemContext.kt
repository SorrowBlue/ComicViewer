package com.sorrowblue.comicviewer.file.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@GraphExtension(GridSizeItemScope::class)
interface FileAppBarItemContext {
    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createFileAppBarItemContext(): FileAppBarItemContext
    }
}

@Composable
fun rememberManageFolderDisplaySettingsUseCase(): ManageFolderDisplaySettingsUseCase =
    if (LocalInspectionMode.current) {
        // Preview implementation
        remember {
            object : ManageFolderDisplaySettingsUseCase {
                val localSettings = MutableStateFlow(FolderDisplaySettings())

                override val settings: Flow<FolderDisplaySettings> = localSettings.asStateFlow()

                override suspend fun edit(
                    action: (FolderDisplaySettings) -> FolderDisplaySettings,
                ) {
                    localSettings.value = action(localSettings.value)
                }
            }
        }
    } else {
        val factory = LocalPlatformContext.current.require<FileAppBarItemContext.Factory>()
        rememberRetained {
            factory.createFileAppBarItemContext()
        }.manageFolderDisplaySettingsUseCase
    }
