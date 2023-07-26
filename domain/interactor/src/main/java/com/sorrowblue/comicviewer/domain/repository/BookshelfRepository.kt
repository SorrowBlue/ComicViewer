package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.file.IFolder
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.framework.Resource
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface BookshelfRepository {

    sealed interface Error: Resource.AppError {
        data object NotFound : Error
        data object Network : Error
        data object System : Error
    }

    /**
     * Connect
     *
     * @param bookshelf
     * @param path
     * @return
     */
    fun connect(bookshelf: Bookshelf, path: String): Flow<Resource<Unit, Error>>

    /**
     * Register
     *
     * @param bookshelf
     * @param folder
     * @return
     */
    fun register(bookshelf: Bookshelf, folder: IFolder): Flow<Resource<Bookshelf, Error>>

    /**
     * Find
     *
     * @param bookshelfId
     * @return
     */
    fun find(bookshelfId: BookshelfId): Flow<Resource<Bookshelf, Error>>

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>>

    suspend fun exists(
        bookshelf: Bookshelf,
        path: String
    ): Result<Boolean, BookshelfRepositoryStatus>

    suspend fun registerOrUpdate(
        bookshelf: Bookshelf,
        path: String
    ): Result<Bookshelf, BookshelfRepositoryStatus>

    fun get(bookshelfId: BookshelfId): Flow<Result<Bookshelf, LibraryStatus>>
    suspend fun delete(bookshelf: Bookshelf): Response<Boolean>
}
