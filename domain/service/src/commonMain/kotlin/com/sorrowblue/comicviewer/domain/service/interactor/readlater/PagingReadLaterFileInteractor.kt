package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.ReadLaterFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.readlater.PagingReadLaterFileUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(DataScope::class)
internal class PagingReadLaterFileInteractor(
    private val readLaterFileLocalDataSource: ReadLaterFileLocalDataSource,
) : PagingReadLaterFileUseCase() {
    override fun run(request: Request): Flow<PagingData<File>> =
        readLaterFileLocalDataSource.pagingDataFlow(request.pagingConfig)
}
