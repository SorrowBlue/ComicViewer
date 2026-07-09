/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class DeleteCollectionUseCase :
    OneShotUseCase<DeleteCollectionUseCase.Request, Unit, Unit>() {
    data class Request(val id: CollectionId) : OneShotUseCase.Request
}
