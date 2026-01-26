package com.sorrowblue.comicviewer.feature.bookshelf.info

import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.Scope

@Scope
annotation class BookshelfInfoScreenScope

expect interface BookshelfInfoScreenContext : ScreenContext {
    val bookshelfInfoUseCase: GetBookshelfInfoUseCase
    val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase
    val scanBookshelfUseCase: ScanBookshelfUseCase
    val regenerateThumbnailsUseCase: RegenerateThumbnailsUseCase

    fun interface Factory {
        fun createBookshelfInfoScreenContext(): BookshelfInfoScreenContext
    }
}
