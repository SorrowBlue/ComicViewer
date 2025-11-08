package com.sorrowblue.comicviewer.feature.bookshelf

import com.sorrowblue.comicviewer.domain.usecase.bookshelf.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class BookshelfScreenScope

@GraphExtension(BookshelfScreenScope::class)
interface BookshelfScreenContext : ScreenContext {
    val pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createBookshelfScreenContext(): BookshelfScreenContext
    }
}
