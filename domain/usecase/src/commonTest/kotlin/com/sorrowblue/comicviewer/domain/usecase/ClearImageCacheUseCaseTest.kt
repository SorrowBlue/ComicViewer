/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.cache.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.cache.ImageCache
import com.sorrowblue.comicviewer.domain.model.cache.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.cache.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class ClearImageCacheUseCaseTest {

    @Test
    fun clearThumbnailImageCache_whenSuccess_clearsDbCacheKeysAndReturnsSuccess() = runTest {
        val bookshelfId = BookshelfId(1)
        val thumbnailCache = ThumbnailImageCache(size = 100L, maxSize = 1000L)
        var clearedBookshelfId: BookshelfId? = null

        val fileRepository = object : StubFileRepository() {
            override suspend fun clearCacheKey(bookshelfId: BookshelfId) {
                clearedBookshelfId = bookshelfId
            }
        }

        val useCase = ClearImageCacheUseCaseImpl(
            imageCacheRepository = StubImageCacheRepository(),
            fileRepository = fileRepository,
        )

        val result = useCase(
            ClearImageCacheUseCase.BookshelfRequest(bookshelfId, thumbnailCache),
        )

        assertTrue(result is Resource.Success)
        assertEquals(bookshelfId, clearedBookshelfId)
    }

    @Test
    fun clearThumbnailImageCache_whenRepositoryFails_doesNotClearDbCacheKeysAndReturnsError() =
        runTest {
            val bookshelfId = BookshelfId(1)
            val thumbnailCache = ThumbnailImageCache(size = 100L, maxSize = 1000L)
            var clearCacheKeyCalled = false

            val imageCacheRepository = object : StubImageCacheRepository() {
                override suspend fun clearImageCache(
                    bookshelfId: BookshelfId,
                    imageCache: ImageCache,
                ): Resource<Unit, Resource.SystemError> =
                    Resource.Error(Resource.SystemError(RuntimeException("Disk error")))
            }

            val fileRepository = object : StubFileRepository() {
                override suspend fun clearCacheKey(bookshelfId: BookshelfId) {
                    clearCacheKeyCalled = true
                }
            }

            val useCase = ClearImageCacheUseCaseImpl(
                imageCacheRepository = imageCacheRepository,
                fileRepository = fileRepository,
            )

            val result = useCase(
                ClearImageCacheUseCase.BookshelfRequest(bookshelfId, thumbnailCache),
            )

            assertTrue(result is Resource.Error)
            assertFalse(clearCacheKeyCalled)
        }

    @Test
    fun clearBookPageImageCache_whenSuccess_doesNotClearDbCacheKeysAndReturnsSuccess() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookPageCache = BookPageImageCache(size = 200L, maxSize = 2000L)
        var clearCacheKeyCalled = false

        val fileRepository = object : StubFileRepository() {
            override suspend fun clearCacheKey(bookshelfId: BookshelfId) {
                clearCacheKeyCalled = true
            }
        }

        val useCase = ClearImageCacheUseCaseImpl(
            imageCacheRepository = StubImageCacheRepository(),
            fileRepository = fileRepository,
        )

        val result = useCase(
            ClearImageCacheUseCase.BookshelfRequest(bookshelfId, bookPageCache),
        )

        assertTrue(result is Resource.Success)
        assertFalse(clearCacheKeyCalled)
    }

    @Test
    fun clearOtherImageCache_clearsOtherCacheAndReturnsSuccess() = runTest {
        var otherCacheCleared = false
        val imageCacheRepository = object : StubImageCacheRepository() {
            override suspend fun clearImageCache() {
                otherCacheCleared = true
            }
        }

        val useCase = ClearImageCacheUseCaseImpl(
            imageCacheRepository = imageCacheRepository,
            fileRepository = StubFileRepository(),
        )

        val result = useCase(ClearImageCacheUseCase.OtherRequest)

        assertTrue(result is Resource.Success)
        assertTrue(otherCacheCleared)
    }

    @Test
    fun clearOtherImageCache_whenRepositoryThrowsException_returnsError() = runTest {
        val imageCacheRepository = object : StubImageCacheRepository() {
            override suspend fun clearImageCache(): Unit =
                throw IllegalStateException("Disk deletion failure")
        }

        val useCase = ClearImageCacheUseCaseImpl(
            imageCacheRepository = imageCacheRepository,
            fileRepository = StubFileRepository(),
        )

        val result = useCase(ClearImageCacheUseCase.OtherRequest)

        assertEquals(Resource.Error(Unit), result)
    }

    @Test
    fun clearOtherImageCache_whenCancelled_rethrowsCancellationException() = runTest {
        val imageCacheRepository = object : StubImageCacheRepository() {
            override suspend fun clearImageCache(): Unit = throw CancellationException("Cancelled")
        }

        val useCase = ClearImageCacheUseCaseImpl(
            imageCacheRepository = imageCacheRepository,
            fileRepository = StubFileRepository(),
        )

        assertFailsWith<CancellationException> {
            useCase(ClearImageCacheUseCase.OtherRequest)
        }
    }

    @Test
    fun getOtherImageCacheInfo_whenRepositoryThrowsUnexpectedException_reportsFatalErrorAndReturnsError() =
        runTest {
            val fatalErrorUseCase = StubSendFatalErrorUseCase()
            val expectedException = IllegalStateException("Unexpected exception")
            val useCase = GetOtherImageCacheInfoUseCaseImpl(
                imageCacheRepository = object : StubImageCacheRepository() {
                    override fun getOtherImageCache(): Resource<OtherImageCache, Resource.SystemError> =
                        throw expectedException
                },
                sendFatalErrorUseCase = fatalErrorUseCase,
            )

            val result = useCase(Unit)
            assertEquals(Resource.Error(Unit), result)
            assertEquals(listOf<Throwable>(expectedException), fatalErrorUseCase.reportedThrowables)
        }
}
