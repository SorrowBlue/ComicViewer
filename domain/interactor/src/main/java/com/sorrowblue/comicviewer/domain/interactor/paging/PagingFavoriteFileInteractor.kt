package com.sorrowblue.comicviewer.domain.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.repository.FavoriteFileRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingFavoriteFileInteractor @Inject constructor(
    private val favoriteFileRepository: FavoriteFileRepository,
) : PagingFavoriteFileUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return favoriteFileRepository.pagingDataFlow(request.pagingConfig, request.favoriteId)
    }
}
