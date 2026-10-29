/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

/**
 * Functional interface for use cases that return a stream of paginated data [PagingData].
 *
 * @param R The request parameter type.
 * @param S The paginated item type.
 */
fun interface PagingUseCase<R : Any, S : Any> {
    /**
     * Observes paginated data for the given [request].
     *
     * @param request The pagination request parameters.
     * @return A [Flow] emitting [PagingData] of [S].
     */
    operator fun invoke(request: R): Flow<PagingData<S>>
}
