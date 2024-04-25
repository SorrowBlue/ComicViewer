package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteThumbnailsUseCase
import javax.inject.Inject

internal class DeleteThumbnailsInteractor @Inject constructor(
    private val fileLocalDataSource: FileLocalDataSource,
    private val imageCacheDataSource: ImageCacheDataSource,
) : DeleteThumbnailsUseCase() {

    override suspend fun run(request: Request): Result<Unit, GetLibraryInfoError> {
        fileLocalDataSource.deleteThumbnails()
        imageCacheDataSource.deleteThumbnails()
        return Result.Success(Unit)
    }
}
