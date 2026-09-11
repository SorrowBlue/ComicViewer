/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.sync

import com.sorrowblue.comicviewer.domain.service.bookshelf.BookshelfScanManager
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
internal class IosBookshelfScanManager(
    scanBookshelfUseCase: ScanBookshelfUseCase,
    regenerateThumbnailsUseCase: RegenerateThumbnailsUseCase,
) : BookshelfScanManager by CoroutineBookshelfScanManager(
    scanBookshelfUseCase = scanBookshelfUseCase,
    regenerateThumbnailsUseCase = regenerateThumbnailsUseCase,
)
