/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.framework.notification.DesktopNotification
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_title_file_scan_completed
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_title_thumbnail_scan_completed
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.getString

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
internal class JvmBookshelfScanManager(
    scanBookshelfUseCase: ScanBookshelfUseCase,
    regenerateThumbnailsUseCase: RegenerateThumbnailsUseCase,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val desktopNotification: DesktopNotification,
) : BookshelfScanManager {

    private val delegate = CoroutineBookshelfScanManager(
        scanBookshelfUseCase = scanBookshelfUseCase,
        regenerateThumbnailsUseCase = regenerateThumbnailsUseCase,
        onFileScanComplete = { bookshelfId ->
            val displayName = getDisplayName(bookshelfId)
            desktopNotification.notify(
                getString(Res.string.bookshelf_info_notification_title_file_scan_completed),
                displayName,
            )
        },
        onThumbnailScanComplete = { bookshelfId ->
            val displayName = getDisplayName(bookshelfId)
            desktopNotification.notify(
                getString(Res.string.bookshelf_info_notification_title_thumbnail_scan_completed),
                displayName,
            )
        },
    )

    override fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean> =
        delegate.isScanningFile(bookshelfId)

    override fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean> =
        delegate.isScanningThumbnail(bookshelfId)

    override fun scanFile(bookshelfId: BookshelfId) {
        delegate.scanFile(bookshelfId)
    }

    override fun scanThumbnail(bookshelfId: BookshelfId) {
        delegate.scanThumbnail(bookshelfId)
    }

    private suspend fun getDisplayName(bookshelfId: BookshelfId): String =
        getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId))
            .first()
            .dataOrNull()?.bookshelf?.displayName.orEmpty()
}
