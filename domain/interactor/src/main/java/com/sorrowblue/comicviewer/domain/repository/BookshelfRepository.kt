package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryError
import com.sorrowblue.comicviewer.framework.Resource
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface BookshelfRepository {

    sealed interface Error {
        data object InvalidPath : Error
        data object InvalidAuth : Error
        data object InvalidServer : Error
        data object Unknown : Error
        data object NoNetwork : Error
    }

    fun connect(bookshelf: Bookshelf, path: String): Flow<Resource<Unit, Error>>

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>>

    suspend fun exists(
        bookshelf: Bookshelf,
        path: String
    ): Result<Boolean, BookshelfRepositoryStatus>

    suspend fun registerOrUpdate(
        bookshelf: Bookshelf,
        path: String
    ): Result<Bookshelf, RegisterLibraryError>

    fun get(bookshelfId: BookshelfId): Flow<Result<Bookshelf, LibraryStatus>>
    suspend fun delete(bookshelf: Bookshelf): Response<Boolean>
    suspend fun register(
        bookshelf: Bookshelf,
        folder: Folder
    ): Result<Bookshelf, BookshelfRepositoryError>
}
