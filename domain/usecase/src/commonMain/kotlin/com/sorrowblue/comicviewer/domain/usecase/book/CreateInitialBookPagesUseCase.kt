/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.service.book.BookPageLayoutService
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Use case that generates the initial list of pages for a comic book.
 */
abstract class CreateInitialBookPagesUseCase :
    OneShotUseCase<CreateInitialBookPagesUseCase.Request, List<BookPage>, Unit>() {

    /**
     * Request parameters for generating the initial page list.
     *
     * @property totalPageCount Total number of pages in the book.
     * @property pageFormat Page display format setting.
     * @property isCompactWindow Whether the current window is compact (e.g., smartphones).
     */
    data class Request(
        val totalPageCount: Int,
        val pageFormat: BookSettings.PageFormat,
        val isCompactWindow: Boolean,
    )
}

@Inject
@ContributesBinding(AppScope::class)
internal class CreateInitialBookPagesUseCaseImpl(private val service: BookPageLayoutService) :
    CreateInitialBookPagesUseCase() {

    override suspend fun run(request: Request): Resource<List<BookPage>, Unit> =
        runCatchingCancellable {
            service.createInitialPages(
                totalPageCount = request.totalPageCount,
                pageFormat = request.pageFormat,
                isCompactWindow = request.isCompactWindow,
            )
        }.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error(Unit) },
        )
}
