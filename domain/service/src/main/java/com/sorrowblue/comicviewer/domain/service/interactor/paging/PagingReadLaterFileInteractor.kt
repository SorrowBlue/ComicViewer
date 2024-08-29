package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.readlater.PagingReadLaterFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingReadLaterFileInteractor @Inject constructor(
    private val readLaterFileLocalDataSource: ReadLaterFileLocalDataSource,
) : PagingReadLaterFileUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return readLaterFileLocalDataSource.pagingDataFlow(request.pagingConfig)
    }
}
