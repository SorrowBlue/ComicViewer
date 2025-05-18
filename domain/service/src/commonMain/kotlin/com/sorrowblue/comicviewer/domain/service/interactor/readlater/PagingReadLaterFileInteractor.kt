package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.readlater.PagingReadLaterFileUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
internal class PagingReadLaterFileInteractor(
    private val readLaterFileLocalDataSource: ReadLaterFileLocalDataSource,
) : PagingReadLaterFileUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return readLaterFileLocalDataSource.pagingDataFlow(request.pagingConfig)
    }
}
