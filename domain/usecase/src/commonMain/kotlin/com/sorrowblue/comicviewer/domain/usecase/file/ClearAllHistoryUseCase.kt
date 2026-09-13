/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.Inject

@Inject
class ClearAllHistoryUseCase(private val fileRepository: FileRepository) :
    OneShotUseCase<ClearAllHistoryUseCase.Request, Unit, Unit>() {

    data object Request : OneShotUseCase.Request

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        fileRepository.deleteAllHistory()
        return Resource.Success(Unit)
    }
}
