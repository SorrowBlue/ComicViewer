/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.sync

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.service.bookshelf.BookshelfScanManager
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

internal class CoroutineBookshelfScanManager(
    private val scanBookshelfUseCase: ScanBookshelfUseCase,
    private val regenerateThumbnailsUseCase: RegenerateThumbnailsUseCase,
    private val onFileScanComplete: suspend (BookshelfId) -> Unit = {},
    private val onThumbnailScanComplete: suspend (BookshelfId) -> Unit = {},
) : BookshelfScanManager {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val scanningFiles = MutableStateFlow<Set<BookshelfId>>(emptySet())
    private val scanningThumbnails = MutableStateFlow<Set<BookshelfId>>(emptySet())

    override fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean> =
        scanningFiles.map { bookshelfId in it }

    override fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean> =
        scanningThumbnails.map { bookshelfId in it }

    override fun scanFile(bookshelfId: BookshelfId) {
        scanningFiles.update { it + bookshelfId }
        coroutineScope.launch {
            try {
                scanBookshelfUseCase(
                    ScanBookshelfUseCase.Request(bookshelfId = bookshelfId) { _, _ -> },
                )
                onFileScanComplete(bookshelfId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logcat(LogPriority.ERROR) { "Error scanning file: ${e.asLog()}" }
            } finally {
                scanningFiles.update { it - bookshelfId }
            }
        }
    }

    override fun scanThumbnail(bookshelfId: BookshelfId) {
        scanningThumbnails.update { it + bookshelfId }
        coroutineScope.launch {
            try {
                regenerateThumbnailsUseCase(
                    RegenerateThumbnailsUseCase.Request(bookshelfId = bookshelfId) { _, _, _ -> },
                )
                onThumbnailScanComplete(bookshelfId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logcat(LogPriority.ERROR) { "Error scanning thumbnail: ${e.asLog()}" }
            } finally {
                scanningThumbnails.update { it - bookshelfId }
            }
        }
    }
}
