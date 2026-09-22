/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

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
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryQueryError
import com.sorrowblue.comicviewer.domain.repository.ThumbnailRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class RegenerateThumbnailsUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val testBookshelf = DeviceStorage(bookshelfId, "TestShelf", 0, false)

    private fun createDummyBook(id: Int): BookFile = BookFile(
        bookshelfId = bookshelfId,
        name = "Book$id.zip",
        parent = "/root",
        path = "/root/Book$id.zip",
        size = 1000L,
        lastModifier = 0L,
        isHidden = false,
    )

    @Test
    fun testRegenerateThumbnails_whenBookshelfNotFound_returnsSuccessImmediately() = runTest {
        val bookshelfRepo = object : TestFakeBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(null)
        }
        val fileRepo = object : TestFakeFileRepository() {}
        val thumbnailRepo = TestFakeThumbnailRepository()

        val useCase = RegenerateThumbnailsUseCaseImpl(
            bookshelfRepository = bookshelfRepo,
            fileRepository = fileRepo,
            thumbnailRepository = thumbnailRepo,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = useCase(RegenerateThumbnailsUseCase.Request(bookshelfId) { _, _, _ -> })
        assertTrue(result is Resource.Success)
        assertEquals(0, thumbnailRepo.loadedThumbnails.size)
    }

    @Test
    fun testRegenerateThumbnails_whenEmptyFiles_returnsSuccessWithoutLoading() = runTest {
        val bookshelfRepo = object : TestFakeBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }
        val fileRepo = object : TestFakeFileRepository() {
            override suspend fun count(bookshelfId: BookshelfId): Long = 0L
        }
        val thumbnailRepo = TestFakeThumbnailRepository()

        val useCase = RegenerateThumbnailsUseCaseImpl(
            bookshelfRepository = bookshelfRepo,
            fileRepository = fileRepo,
            thumbnailRepository = thumbnailRepo,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = useCase(RegenerateThumbnailsUseCase.Request(bookshelfId) { _, _, _ -> })
        assertTrue(result is Resource.Success)
        assertEquals(0, thumbnailRepo.loadedThumbnails.size)
    }

    @Test
    fun testRegenerateThumbnails_loadsAllFilesAndReportsProgress() = runTest {
        val files = (1..5).map { createDummyBook(it) }
        val bookshelfRepo = object : TestFakeBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }
        val fileRepo = object : TestFakeFileRepository() {
            override suspend fun count(bookshelfId: BookshelfId): Long = files.size.toLong()
            override suspend fun fileList(
                bookshelfId: BookshelfId,
                limit: Int,
                offset: Long,
            ): List<File> = files.drop(offset.toInt()).take(limit)
        }
        val thumbnailRepo = TestFakeThumbnailRepository()

        val progressUpdates = mutableListOf<Long>()
        val useCase = RegenerateThumbnailsUseCaseImpl(
            bookshelfRepository = bookshelfRepo,
            fileRepository = fileRepo,
            thumbnailRepository = thumbnailRepo,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = useCase(
            RegenerateThumbnailsUseCase.Request(bookshelfId) { _, progress, max ->
                progressUpdates.add(progress)
                assertEquals(files.size.toLong(), max)
            },
        )

        assertTrue(result is Resource.Success)
        assertEquals(files.size, thumbnailRepo.loadedThumbnails.size)
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), progressUpdates)
    }

    @Test
    fun testRegenerateThumbnails_whenSomeThumbnailFails_continuesProcessing() = runTest {
        val files = (1..3).map { createDummyBook(it) }
        val bookshelfRepo = object : TestFakeBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }
        val fileRepo = object : TestFakeFileRepository() {
            override suspend fun count(bookshelfId: BookshelfId): Long = files.size.toLong()
            override suspend fun fileList(
                bookshelfId: BookshelfId,
                limit: Int,
                offset: Long,
            ): List<File> = files.drop(offset.toInt()).take(limit)
        }
        val thumbnailRepo = object : TestFakeThumbnailRepository() {
            override suspend fun load(
                fileThumbnail: FileThumbnail,
            ): Resource<Unit, Resource.SystemError> {
                super.load(fileThumbnail)
                return if (fileThumbnail.path.contains("Book2")) {
                    Resource.Error(
                        Resource.SystemError(IllegalStateException("Failed to load Book2")),
                    )
                } else {
                    Resource.Success(Unit)
                }
            }
        }

        val progressUpdates = mutableListOf<Long>()
        val useCase = RegenerateThumbnailsUseCaseImpl(
            bookshelfRepository = bookshelfRepo,
            fileRepository = fileRepo,
            thumbnailRepository = thumbnailRepo,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = useCase(
            RegenerateThumbnailsUseCase.Request(bookshelfId) { _, progress, max ->
                progressUpdates.add(progress)
                assertEquals(files.size.toLong(), max)
            },
        )

        assertTrue(result is Resource.Success)
        assertEquals(3, thumbnailRepo.loadedThumbnails.size)
        assertEquals(listOf(1L, 2L, 3L), progressUpdates)
    }

    private open class TestFakeThumbnailRepository : ThumbnailRepository {
        val loadedThumbnails = mutableListOf<FileThumbnail>()

        override suspend fun load(
            fileThumbnail: FileThumbnail,
        ): Resource<Unit, Resource.SystemError> {
            loadedThumbnails.add(fileThumbnail)
            return Resource.Success(Unit)
        }
    }

    private open class TestFakeBookshelfRepository : BookshelfRepository {
        override suspend fun updateOrCreate(
            bookshelf: Bookshelf,
            transaction: suspend (Bookshelf) -> Unit,
        ): Bookshelf? = TODO()

        override suspend fun delete(
            bookshelfId: BookshelfId,
        ): Resource<Unit, Resource.SystemError> = TODO()

        override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = TODO()
        override fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>> =
            TODO()

        override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> = TODO()
        override suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean) = TODO()
    }

    private open class TestFakeFileRepository : FileRepository {
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

        override suspend fun addUpdate(fileModel: File) = TODO()
        override suspend fun updateHistory(
            path: String,
            bookshelfId: BookshelfId,
            lastReadPage: Int,
            lastReading: Long,
        ) = TODO()

        override suspend fun updateAdditionalInfo(
            path: String,
            bookshelfId: BookshelfId,
            cacheKey: String,
            totalPage: Int,
        ) = TODO()

        override suspend fun updateSimpleAll(list: List<File>) = TODO()
        override suspend fun updateSimple(list: File): Resource<File, FileRepositoryQueryError> =
            TODO()
        override suspend fun selectByNotPaths(
            bookshelfId: BookshelfId,
            path: String,
            list: List<String>,
        ): List<File> = TODO()

        override suspend fun deleteAll(list: List<File>) = TODO()
        override suspend fun exists(bookshelfId: BookshelfId, path: String): Boolean = TODO()
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

        override suspend fun removeCacheKey(diskCacheKey: String) = TODO()
        override suspend fun root(id: BookshelfId): Folder? = TODO()
        override fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<Book>> =
            TODO()

        override suspend fun deleteThumbnails() = TODO()
        override suspend fun clearCacheKey(bookshelfId: BookshelfId) = TODO()
        override suspend fun deleteHistory(bookshelfId: BookshelfId, list: List<String>): Unit =
            TODO()
        override suspend fun deleteAllHistory(): Unit = TODO()
        override suspend fun updateHistory(file: File, files: List<File>): Unit = TODO()
        override suspend fun deleteAll2(bookshelfModelId: BookshelfId): Unit = TODO()
        override suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String> = TODO()
        override fun lastHistory(): Flow<File?> = TODO()
        override suspend fun fileList(
            bookshelfId: BookshelfId,
            limit: Int,
            offset: Long,
        ): List<File> = TODO()

        override suspend fun count(bookshelfId: BookshelfId): Long = TODO()
        override suspend fun updateFileType(file: File) = TODO()
    }
}
