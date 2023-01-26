package com.sorrowblue.comicviewer.domain.usecase.paging.interactor

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.repository.FavoriteBookRepository
import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingFileInteractor @Inject constructor(
    private val repository: FileRepository,
) : PagingFileUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return repository.pagingDataFlow(request.pagingConfig, request.server, request.bookshelf)
    }
}

internal class PagingFavoriteInteractor @Inject constructor(
    private val repository: FavoriteRepository,
) : PagingFavoriteUseCase() {

    override fun run(request: Request): Flow<PagingData<Favorite>> {
        return repository.pagingSourceCount(request.pagingConfig)
    }
}

internal class PagingFavoriteBookInteractor @Inject constructor(
    private val repository: FavoriteBookRepository,
) : PagingFavoriteBookUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return repository.pagingSource(request.pagingConfig, request.favoriteId)
    }
}
