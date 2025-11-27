package com.sorrowblue.comicviewer.feature.bookshelf.edit

import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class BookshelfEditScreenScope

@GraphExtension(BookshelfEditScreenScope::class)
interface BookshelfEditScreenContext : ScreenContext {
    val getBookshelfInfoUseCase: GetBookshelfInfoUseCase
    val registerBookshelfUseCase: RegisterBookshelfUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createBookshelfEditScreenContext(): BookshelfEditScreenContext
    }
}
