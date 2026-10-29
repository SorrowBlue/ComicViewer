/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.book

import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.service.book.BookPageLayoutService
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Use case that recalculates the combined or split page layout once an image's aspect ratio is determined.
 */
abstract class ResolveBookPageLayoutUseCase :
    OneShotUseCase<ResolveBookPageLayoutUseCase.Request, List<PageItem>, Unit>() {

    /**
     * Request parameters for updating the page list layout.
     *
     * @property currentList Current list of page items.
     * @property unratedPage The unrated page whose aspect ratio has been determined.
     * @property isPortrait `true` if the image is portrait or square, `false` if landscape.
     */
    data class Request(
        val currentList: List<PageItem>,
        val unratedPage: UnratedPage,
        val isPortrait: Boolean,
    )
}

@Inject
@ContributesBinding(AppScope::class)
internal class ResolveBookPageLayoutUseCaseImpl(private val service: BookPageLayoutService) :
    ResolveBookPageLayoutUseCase() {

    override suspend fun run(request: Request): Resource<List<PageItem>, Unit> =
        runCatchingCancellable {
            service.resolvePage(
                currentList = request.currentList,
                unratedPage = request.unratedPage,
                isPortrait = request.isPortrait,
            )
        }.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error(Unit) },
        )
}
