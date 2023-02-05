package com.sorrowblue.comicviewer.domain.usecase.paging.interactor

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.repository.FavoriteBookRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteBookUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingFavoriteBookInteractor @Inject constructor(
    private val favoriteBookRepository: FavoriteBookRepository,
) : PagingFavoriteBookUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return favoriteBookRepository.pagingDataFlow(request.pagingConfig, request.favoriteId)
    }
}
