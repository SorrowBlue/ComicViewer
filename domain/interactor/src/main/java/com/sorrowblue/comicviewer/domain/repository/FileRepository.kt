package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.Scan
import com.sorrowblue.comicviewer.domain.entity.SearchCondition
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.file.IFolder
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.framework.Resource
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    sealed interface Error : Resource.AppError {
        data object System : Error
    }

    fun addReadLater(bookshelfId: BookshelfId, path: String): Flow<Resource<Unit, Error>>
    fun deleteReadLater(bookshelfId: BookshelfId, path: String): Flow<Resource<Unit, Error>>

    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        searchCondition: () -> SearchCondition
    ): Flow<PagingData<File>>

    fun find(bookshelfId: BookshelfId, path: String): Flow<Resource<File, Error>>

    fun findByParent(bookshelfId: BookshelfId, parent: String): Flow<Resource<File, Error>>

    suspend fun update(
        bookshelfId: BookshelfId,
        path: String,
        lastReadPage: Int,
        lastReadTime: Long
    )

    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        folder: IFolder,
    ): Flow<PagingData<File>>

    suspend fun get(bookshelfId: BookshelfId, path: String): Response<File?>
    fun getFile(bookshelfId: BookshelfId, path: String): Flow<Result<File, Unit>>
    suspend fun getBook(bookshelfId: BookshelfId, path: String): Response<Book?>
    suspend fun scan(folder: IFolder, scan: Scan): String

    suspend fun get2(bookshelfId: BookshelfId, path: String): Result<File?, Unit>
    suspend fun getRoot(bookshelfId: BookshelfId): Result<File?, Unit>
    fun getNextRelFile(
        bookshelfId: BookshelfId,
        path: String,
        isNext: Boolean
    ): Flow<Result<File, Unit>>

    suspend fun getFolder(bookshelf: Bookshelf, path: String): Result<Folder, FileRepositoryError>

    fun pagingHistoryBookFlow(pagingConfig: PagingConfig): Flow<PagingData<File>>

    suspend fun deleteThumbnails()

    suspend fun deleteHistory(bookshelfId: BookshelfId, list: List<String>)
}
