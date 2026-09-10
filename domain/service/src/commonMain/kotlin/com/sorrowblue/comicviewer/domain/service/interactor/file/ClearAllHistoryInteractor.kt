/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.file.ClearAllHistoryUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class ClearAllHistoryInteractor(private val fileRepository: FileRepository) :
    ClearAllHistoryUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> {
        fileRepository.deleteAllHistory()
        return Resource.Success(Unit)
    }
}
