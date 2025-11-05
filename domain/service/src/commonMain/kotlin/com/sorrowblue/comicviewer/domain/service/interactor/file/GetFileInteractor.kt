package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Inject
internal class GetFileInteractor(
    private val fileLocalDataSource: FileLocalDataSource,
) : GetFileUseCase() {

    override fun run(request: Request): Flow<Resource<File, Error>> {
        return flow {
            runCatching {
                fileLocalDataSource.findBy(request.bookshelfId, request.path)
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
}
