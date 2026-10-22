/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryQueryError
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReader
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class GetBookUseCaseTest {

    @Test
    fun getBook_updatesDatabaseOnceWithTotalPageCount() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(
            id = bookshelfId,
            displayName = "Test",
            fileCount = 0,
            isDeleted = false,
        )
        val path = "/manga/vol1.cbz"

        var updateSimpleCount = 0
        var addUpdateCount = 0
        var updatedFileArg: File? = null

        val remoteBook = BookFile(
            bookshelfId = bookshelfId,
            name = "vol1.cbz",
            parent = "/manga",
            path = path,
            size = 2048L,
            lastModifier = 500L,
            isHidden = false,
            totalPageCount = 0,
        )

        val bookshelfRepository = object : BookshelfRepository {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            override suspend fun updateOrCreate(
                bookshelf: Bookshelf,
                transaction: suspend (Bookshelf) -> Unit,
            ): Bookshelf? = TODO()

            override fun pagingSource(
                pagingConfig: PagingConfig,
            ): Flow<PagingData<BookshelfFolder>> = TODO()

            override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                TODO()

            override suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean): Unit =
                TODO()
        }

        val fileRepository = object : FileRepository {
            override suspend fun updateSimple(
                list: File,
            ): Resource<File, FileRepositoryQueryError> {
                updateSimpleCount++
                updatedFileArg = list
                return Resource.Success(list)
            }

            override suspend fun addUpdate(fileModel: File) {
                addUpdateCount++
            }

            override fun pagingDataFlow(
                pagingConfig: PagingConfig,
                bookshelf: Bookshelf,
                file: File,
                searchCondition: () -> SearchCondition,
            ): Flow<PagingData<File>> = TODO()

            override fun pagingSourceBookThumbnail(
                pagingConfig: PagingConfig,
                bookshelf: Bookshelf,
                file: File,
                searchCondition: () -> SearchCondition,
            ): Flow<PagingData<BookThumbnail>> = TODO()

            override fun pagingDataFlow(
                pagingConfig: PagingConfig,
                bookshelfId: BookshelfId?,
                searchCondition: () -> SearchCondition,
            ): Flow<PagingData<File>> = TODO()

            override suspend fun updateHistory(
                path: String,
                bookshelfId: BookshelfId,
                lastReadPage: Int,
                lastReading: Long,
            ): Unit = TODO()

            override suspend fun updateAdditionalInfo(
                path: String,
                bookshelfId: BookshelfId,
                cacheKey: String,
                totalPage: Int,
            ): Unit = TODO()

            override fun pagingSource(
                bookshelfId: BookshelfId,
                pagingConfig: PagingConfig,
            ): Flow<PagingData<BookThumbnail>> = TODO()

            override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> = TODO()
            override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? = TODO()
            override fun nextFileModel(
                bookshelfId: BookshelfId,
                path: String,
                sortType: SortType,
            ): Flow<File?> = TODO()

            override fun nextFileModel(
                bookshelfId: BookshelfId,
                path: String,
                searchCondition: SearchCondition,
                sortType: SortType,
            ): Flow<File?> = TODO()

            override fun prevFileModel(
                bookshelfId: BookshelfId,
                path: String,
                sortType: SortType,
            ): Flow<File?> = TODO()

            override fun prevFileModel(
                bookshelfId: BookshelfId,
                path: String,
                searchCondition: SearchCondition,
                sortType: SortType,
            ): Flow<File?> = TODO()

            override suspend fun getCacheKeys(
                bookshelfId: BookshelfId,
                parent: String,
                limit: Int,
                folderThumbnailOrderModel: FolderThumbnailOrder,
            ): List<String> = TODO()

            override suspend fun removeCacheKey(diskCacheKey: String): Unit = TODO()
            override suspend fun root(id: BookshelfId): Folder? = TODO()
            override fun pagingHistoryBookSource(
                pagingConfig: PagingConfig,
            ): Flow<PagingData<Book>> = TODO()

            override suspend fun clearCacheKey(bookshelfId: BookshelfId): Unit = TODO()
            override suspend fun deleteAllHistory(): Unit = TODO()
            override suspend fun updateHistory(file: File, files: List<File>): Unit = TODO()
            override suspend fun deleteAll(bookshelfModelId: BookshelfId): Unit = TODO()
            override suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String> = TODO()
            override fun lastHistory(): Flow<File?> = TODO()
            override suspend fun fileList(
                bookshelfId: BookshelfId,
                limit: Int,
                offset: Long,
            ): List<File> = TODO()

            override suspend fun count(bookshelfId: BookshelfId): Long = TODO()
            override suspend fun updateFileType(file: File): Unit = TODO()
        }

        val remoteStorageClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient =
                object : RemoteStorageClient {
                    override suspend fun file(path: String, resolveImageFolder: Boolean): File =
                        remoteBook

                    override suspend fun pageCount(book: Book): Int = 180
                    override suspend fun connect(path: String): Unit = TODO()
                    override suspend fun exists(path: String): Boolean = TODO()
                    override suspend fun listFiles(
                        file: File,
                        resolveImageFolder: Boolean,
                        filter: (File) -> Boolean,
                    ): List<File> = TODO()

                    override suspend fun fileReader(book: Book): BookFileReader = TODO()
                    override suspend fun getAttribute(path: String): FileAttribute? = TODO()
                    override suspend fun getFileSize(path: String): Long = TODO()
                }
        }

        val manageFolderSettingsUseCase = object : ManageFolderSettingsUseCase {
            override val settings: Flow<FolderSettings> = flowOf(FolderSettings())
            override suspend fun edit(action: (FolderSettings) -> FolderSettings) = Unit
        }

        val useCase = GetBookUseCaseImpl(
            bookshelfRepository = bookshelfRepository,
            fileRepository = fileRepository,
            remoteStorageClientFactory = remoteStorageClientFactory,
            manageFolderSettingsUseCase = manageFolderSettingsUseCase,
        )

        val result = useCase(GetBookUseCase.Request(bookshelfId, path)).first()

        assertTrue(result is Resource.Success)
        assertEquals(180, result.data.totalPageCount)
        assertEquals(1, updateSimpleCount)
        assertEquals(0, addUpdateCount)
        assertEquals(180, (updatedFileArg as Book).totalPageCount)
    }
}
