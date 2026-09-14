/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

fun interface PagingUseCase<R : Any, S : Any> {
    operator fun invoke(request: R): Flow<PagingData<S>>
}
