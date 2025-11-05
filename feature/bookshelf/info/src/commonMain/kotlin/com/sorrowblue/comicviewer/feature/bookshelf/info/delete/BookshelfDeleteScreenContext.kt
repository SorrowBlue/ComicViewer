package com.sorrowblue.comicviewer.feature.bookshelf.info.delete

import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class BookshelfDeleteScreenScope

@GraphExtension(BookshelfDeleteScreenScope::class)
interface BookshelfDeleteScreenContext : ScreenContext {
    val getBookshelfInfoUseCase: GetBookshelfInfoUseCase
    val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createBookshelfDeleteScreenContext(): BookshelfDeleteScreenContext
    }
}
