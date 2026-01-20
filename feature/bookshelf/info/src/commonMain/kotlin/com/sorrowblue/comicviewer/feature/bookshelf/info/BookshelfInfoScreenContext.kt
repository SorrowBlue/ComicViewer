package com.sorrowblue.comicviewer.feature.bookshelf.info

import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class BookshelfInfoScreenScope

@GraphExtension(BookshelfInfoScreenScope::class)
expect interface BookshelfInfoScreenContext : ScreenContext {
    val bookshelfInfoUseCase: GetBookshelfInfoUseCase
    val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase
    val scanBookshelfUseCase: ScanBookshelfUseCase
    val regenerateThumbnailsUseCase: RegenerateThumbnailsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createBookshelfInfoScreenContext(): BookshelfInfoScreenContext
    }
}
