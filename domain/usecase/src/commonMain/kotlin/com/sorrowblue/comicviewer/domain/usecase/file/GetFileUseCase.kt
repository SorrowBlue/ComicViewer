/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Inject
class GetFileUseCase(private val fileRepository: FileRepository) :
    UseCase<GetFileUseCase.Request, File, GetFileUseCase.Error>() {

    data class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

    enum class Error : Resource.AppError {
        NOT_FOUND,
    }

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
