package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow

interface FileRepository {

    suspend fun update(bookshelfId: BookshelfId, path: String, lastReadPage: Int, lastReadTime: Long)

    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        folder: Folder,
    ): Flow<PagingData<File>>

    suspend fun get(bookshelfId: BookshelfId, path: String): Response<File?>
    fun getFile(bookshelfId: BookshelfId, path: String): Flow<Result<File, Unit>>
    suspend fun getBook(bookshelfId: BookshelfId, path: String): Response<Book?>
    suspend fun scan(folder: Folder, scanType: ScanType): String
    fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        query: () -> String
    ): Flow<PagingData<File>>

    suspend fun get2(bookshelfId: BookshelfId, path: String): Result<File?, Unit>
    suspend fun getRoot(bookshelfId: BookshelfId): Result<File?, Unit>
    fun getNextRelFile(
        bookshelfId: BookshelfId,
        path: String,
        isNext: Boolean
    ): Flow<Result<File, Unit>>

    suspend fun list(bookshelfId: BookshelfId): List<File>
    suspend fun getFolder(bookshelf: Bookshelf, path: String): Result<Folder, FileRepositoryError>
}
