package com.sorrowblue.comicviewer.feature.settings.folder

import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope


@Scope
annotation class FolderSettingsScreenScope

@GraphExtension(FolderSettingsScreenScope::class)
interface FolderSettingsScreenContext : ScreenContext {

    val manageFolderSettingsUseCase: ManageFolderSettingsUseCase
    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createFolderSettingsScreenContext(): FolderSettingsScreenContext
    }
}
