/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.repository.ThumbnailRepository
import com.sorrowblue.comicviewer.domain.usecase.StubBookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.StubFileRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
        val bookshelfRepo = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(null)
        }
        val fileRepo = object : StubFileRepository() {}
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
        val bookshelfRepo = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }
        val fileRepo = object : StubFileRepository() {
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
        val bookshelfRepo = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }
        val fileRepo = object : StubFileRepository() {
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
        val bookshelfRepo = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }
        val fileRepo = object : StubFileRepository() {
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

    @Test
    fun testRegenerateThumbnails_whenSomeThumbnailThrowsException_continuesProcessing() = runTest {
        val files = (1..3).map { createDummyBook(it) }
        val bookshelfRepo = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }
        val fileRepo = object : StubFileRepository() {
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
                if (fileThumbnail.path.contains("Book2")) {
                    error("Unexpected exception on Book2")
                }
                return Resource.Success(Unit)
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

    @Test
    fun testRegenerateThumbnails_whenFileListThrowsException_returnsSystemError() = runTest {
        val bookshelfRepo = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }
        val fileRepo = object : StubFileRepository() {
            override suspend fun count(bookshelfId: BookshelfId): Long = 5L
            override suspend fun fileList(
                bookshelfId: BookshelfId,
                limit: Int,
                offset: Long,
            ): List<File> = error("DB error")
        }
        val thumbnailRepo = TestFakeThumbnailRepository()

        val useCase = RegenerateThumbnailsUseCaseImpl(
            bookshelfRepository = bookshelfRepo,
            fileRepository = fileRepo,
            thumbnailRepository = thumbnailRepo,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = useCase(RegenerateThumbnailsUseCase.Request(bookshelfId) { _, _, _ -> })
        assertEquals(Resource.Error(RegenerateThumbnailsUseCase.Error.System), result)
    }

    @Test
    fun testRegenerateThumbnails_whenCancelled_rethrowsCancellationException() = runTest {
        val bookshelfRepo = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }
        val fileRepo = object : StubFileRepository() {
            override suspend fun count(bookshelfId: BookshelfId): Long =
                throw CancellationException("Cancelled")
        }
        val thumbnailRepo = TestFakeThumbnailRepository()

        val useCase = RegenerateThumbnailsUseCaseImpl(
            bookshelfRepository = bookshelfRepo,
            fileRepository = fileRepo,
            thumbnailRepository = thumbnailRepo,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        assertFailsWith<CancellationException> {
            useCase(RegenerateThumbnailsUseCase.Request(bookshelfId) { _, _, _ -> })
        }
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
}
