package com.sorrowblue.comicviewer.domain.usecase.paging.interactor

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingFileInteractor @Inject constructor(
    private val fileRepository: FileRepository,
) : PagingFileUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return fileRepository.pagingDataFlow(request.pagingConfig, request.server, request.folder)
    }
}
