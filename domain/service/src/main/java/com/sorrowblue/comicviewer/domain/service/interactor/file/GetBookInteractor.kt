package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.Unknown
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class GetBookInteractor @Inject constructor(
    private val fileLocalDataSource: FileLocalDataSource,
) : GetBookUseCase() {

    override fun run(request: Request): Flow<Result<Book, Unit>> {
        return kotlin.runCatching {
            fileLocalDataSource.flow(request.bookshelfId, request.path)
        }.fold({ fileModelFlow ->
            fileModelFlow.map {
                if (it is Book) Result.Success(it) else Result.Error(Unit)
            }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }
}
