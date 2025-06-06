package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ThumbnailDataSource
import com.sorrowblue.comicviewer.domain.service.limitedCoroutineScope
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Qualifier

@Factory
internal class RegenerateThumbnailsInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val thumbnailDataSource: ThumbnailDataSource,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
) : RegenerateThumbnailsUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Error> {
        val bookshelf = bookshelfLocalDataSource.flow(request.bookshelfId).first()
        if (bookshelf != null) {
            val mutex = Mutex()
            val limit = 1
            var offset = 0L
            val count = fileLocalDataSource.count(request.bookshelfId)
            limitedCoroutineScope(6, context = dispatcher) {
                List(count.toInt()) {
                    async {
                        val list = mutex.withLock {
                            fileLocalDataSource.fileList(
                                request.bookshelfId,
                                limit = limit,
                                offset = offset
                            ).also {
                                offset += it.size
                            }
                        }
                        if (list.isNotEmpty()) {
                            thumbnailDataSource.load(FileThumbnail.from(list.first()))
                                .await()
                            request.process(bookshelf, offset, count)
                        }
                    }
                }.awaitAll()
            }
        }
        return Resource.Success(Unit)
    }
}
