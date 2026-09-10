/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@ContributesBinding(AppScope::class)
internal class GetFileInteractor(private val fileRepository: FileRepository) :
    GetFileUseCase() {
    override fun run(request: Request): Flow<Resource<File, Error>> = flow {
        runCatching {
            fileRepository.findBy(request.bookshelfId, request.path)
        }.fold({
            if (it != null) {
                emit(Resource.Success(it))
            } else {
                emit(Resource.Error(Error.NOT_FOUND))
            }
        }, {
            emit(Resource.Error(Error.NOT_FOUND))
        })
    }
}
