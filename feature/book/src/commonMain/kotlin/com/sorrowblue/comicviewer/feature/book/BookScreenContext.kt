package com.sorrowblue.comicviewer.feature.book

import com.sorrowblue.comicviewer.domain.model.PluginManager
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class BookPrepareScreenScope

@GraphExtension(BookPrepareScreenScope::class)
interface BookScreenContext : ScreenContext {

    val getBookUseCase: GetBookUseCase
    val getNextBookUseCase: GetNextBookUseCase
    val updateLastReadPageUseCase: UpdateLastReadPageUseCase
    val manageBookSettingsUseCase: ManageBookSettingsUseCase
    val pluginManager: PluginManager

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {

        fun createBookScreenContext(): BookScreenContext
    }
}
