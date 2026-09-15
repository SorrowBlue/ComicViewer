/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.cache.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.dataOrNull
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

abstract class GetBookshelfImageCacheInfoUseCase :
    UseCase<Unit, List<BookshelfImageCacheInfo>, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class GetBookshelfImageCacheInfoUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val imageCacheRepository: ImageCacheRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : GetBookshelfImageCacheInfoUseCase() {

    override fun run(request: Unit): Flow<Resource<List<BookshelfImageCacheInfo>, Unit>> =
        bookshelfRepository.allBookshelf().fold(
            onSuccess = { flow -> flow.map { Resource.Success(imageCacheInfoList(it)) } },
            onError = {
                flow {
                    sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                    emit(Resource.Error(Unit))
                }
            },
        )

    private fun imageCacheInfoList(list: List<Bookshelf>): List<BookshelfImageCacheInfo> =
        list.mapNotNull {
            imageCacheRepository.getBookshelfImageCacheInfo(it).dataOrNull()
        }
}
