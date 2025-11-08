package com.sorrowblue.comicviewer.folder

import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class FolderScreenScope

@GraphExtension(FolderScreenScope::class)
interface FolderScreenContext : ScreenContext {
    val getFileUseCase: GetFileUseCase
    val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase
    val pagingFileUseCase: PagingFileUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createFolderScreenContext(): FolderScreenContext
    }
}
