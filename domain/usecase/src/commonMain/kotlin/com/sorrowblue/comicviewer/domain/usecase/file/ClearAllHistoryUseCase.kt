/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

abstract class ClearAllHistoryUseCase : OneShotUseCase<Unit, Unit, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class ClearAllHistoryUseCaseImpl(private val fileRepository: FileRepository) :
    ClearAllHistoryUseCase() {

    override suspend fun run(request: Unit): Resource<Unit, Unit> {
        fileRepository.deleteAllHistory()
        return Resource.Success(Unit)
    }
}
