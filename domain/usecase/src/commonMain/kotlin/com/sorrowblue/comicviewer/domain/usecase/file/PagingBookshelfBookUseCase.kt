/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Paging use case for bookshelf book thumbnails.
 */
fun interface PagingBookshelfBookUseCase :
    PagingUseCase<PagingBookshelfBookUseCase.Request, BookThumbnail> {

    /**
     * Request parameters for [PagingBookshelfBookUseCase].
     *
     * @property bookshelfId The ID of the bookshelf whose book thumbnails are queried.
     * @property pagingConfig Configuration for pagination.
     */
    data class Request(val bookshelfId: BookshelfId, val pagingConfig: PagingConfig)
}

@Inject
@ContributesBinding(AppScope::class)
internal class PagingBookshelfBookUseCaseImpl(private val fileRepository: FileRepository) :
    PagingBookshelfBookUseCase {

    override fun invoke(request: PagingBookshelfBookUseCase.Request) =
        fileRepository.pagingSource(request.bookshelfId, request.pagingConfig)
}
