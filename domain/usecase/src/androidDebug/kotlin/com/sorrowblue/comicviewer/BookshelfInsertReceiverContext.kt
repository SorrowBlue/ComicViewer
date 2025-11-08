package com.sorrowblue.comicviewer

import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class BookshelfInsertReceiverScope

@GraphExtension(BookshelfInsertReceiverScope::class)
interface BookshelfInsertReceiverContext {
    val registerBookshelfUseCase: RegisterBookshelfUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createBookshelfInsertReceiverContext(): BookshelfInsertReceiverContext
    }
}
