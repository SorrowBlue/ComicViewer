package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.GetIntentBookUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import logcat.logcat

internal class GetIntentBookInteractor @Inject constructor(
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
) : GetIntentBookUseCase() {

    override fun run(request: Request): Flow<Resource<BookFile, Error>> {
        val bookshelf = ShareContents
        val remoteDataSource = remoteDataSourceFactory.create(bookshelf)
        return flow {
            logcat { "request.data=${request.data}" }
            var book = remoteDataSource.file(request.data)
            logcat { "book=$book" }
            book = book as BookFile
            book = book.copy(totalPageCount = remoteDataSource.fileReader(book)?.pageCount() ?: 0)
            val resource = Resource.Success(book)
            emit(resource)
        }
    }
}
