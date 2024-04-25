package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileModelLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ExistsReadlaterInteractor @Inject constructor(
    private val readLaterFileModelLocalDataSource: ReadLaterFileModelLocalDataSource,
) : ExistsReadlaterUseCase() {

    override fun run(request: Request): Flow<Resource<Boolean, Error>> {
        return readLaterFileModelLocalDataSource.exists(
            ReadLaterFile(
                request.bookshelfId,
                request.path
            )
        ).map {
            Resource.Success(it)
        }
    }
}
