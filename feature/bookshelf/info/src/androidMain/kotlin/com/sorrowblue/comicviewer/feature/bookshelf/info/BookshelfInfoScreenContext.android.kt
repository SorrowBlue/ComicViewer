package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.work.WorkManager
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@GraphExtension(scope = BookshelfInfoScreenScope::class)
actual interface BookshelfInfoScreenContext : ScreenContext {
    actual val bookshelfInfoUseCase: GetBookshelfInfoUseCase
    actual val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase
    actual val scanBookshelfUseCase: ScanBookshelfUseCase
    actual val regenerateThumbnailsUseCase: RegenerateThumbnailsUseCase
    val workManager: WorkManager

    @ContributesTo(scope = dev.zacsweers.metro.AppScope::class)
    @GraphExtension.Factory
    actual fun interface Factory {
        actual fun createBookshelfInfoScreenContext(): BookshelfInfoScreenContext
    }
}
