/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.cache.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.cache.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.cache.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.cache.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class ImageCacheInfoUseCaseTest {

    private val bookshelf1 = DeviceStorage(BookshelfId(1), "Shelf 1", 10, false)
    private val bookshelf2 = DeviceStorage(BookshelfId(2), "Shelf 2", 20, false)
    private val cacheInfo1 = BookshelfImageCacheInfo(
        bookshelf = bookshelf1,
        thumbnailImageCache = ThumbnailImageCache(size = 100L, maxSize = 1000L),
        bookPageImageCache = BookPageImageCache(size = 200L, maxSize = 2000L),
    )

    @Test
    fun getBookshelfImageCacheInfo_whenBookshelvesExist_returnsCacheInfoList() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val useCase = GetBookshelfImageCacheInfoUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                    Resource.Success(flowOf(listOf(bookshelf1)))
            },
            imageCacheRepository = object : StubImageCacheRepository() {
                override fun getBookshelfImageCacheInfo(
                    bookshelf: Bookshelf,
                ): Resource<BookshelfImageCacheInfo, Resource.SystemError> =
                    Resource.Success(cacheInfo1)
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        val result = useCase().first()

        assertEquals(Resource.Success(listOf(cacheInfo1)), result)
        assertEquals(0, fatalErrorUseCase.reportedThrowables.size)
    }

    @Test
    fun getBookshelfImageCacheInfo_whenDuplicateBookshelfListsEmitted_queriesCacheOnlyOnce() =
        runTest {
            val fatalErrorUseCase = StubSendFatalErrorUseCase()
            var queryCount = 0
            val useCase = GetBookshelfImageCacheInfoUseCaseImpl(
                bookshelfRepository = object : StubBookshelfRepository() {
                    override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                        Resource.Success(flowOf(listOf(bookshelf1), listOf(bookshelf1)))
                },
                imageCacheRepository = object : StubImageCacheRepository() {
                    override fun getBookshelfImageCacheInfo(
                        bookshelf: Bookshelf,
                    ): Resource<BookshelfImageCacheInfo, Resource.SystemError> {
                        queryCount++
                        return Resource.Success(cacheInfo1)
                    }
                },
                sendFatalErrorUseCase = fatalErrorUseCase,
            )

            val results = useCase().toList()

            assertEquals(listOf(Resource.Success(listOf(cacheInfo1))), results)
            assertEquals(1, queryCount)
        }

    @Test
    fun getBookshelfImageCacheInfo_whenOneBookshelfFails_skipsFailedAndReportsFatalError() =
        runTest {
            val fatalErrorUseCase = StubSendFatalErrorUseCase()
            val expectedException = RuntimeException("Cache info error")
            val useCase = GetBookshelfImageCacheInfoUseCaseImpl(
                bookshelfRepository = object : StubBookshelfRepository() {
                    override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                        Resource.Success(flowOf(listOf(bookshelf1, bookshelf2)))
                },
                imageCacheRepository = object : StubImageCacheRepository() {
                    override fun getBookshelfImageCacheInfo(
                        bookshelf: Bookshelf,
                    ): Resource<BookshelfImageCacheInfo, Resource.SystemError> =
                        if (bookshelf.id == bookshelf1.id) {
                            Resource.Success(cacheInfo1)
                        } else {
                            Resource.Error(Resource.SystemError(expectedException))
                        }
                },
                sendFatalErrorUseCase = fatalErrorUseCase,
            )

            val result = useCase().first()

            assertEquals(Resource.Success(listOf(cacheInfo1)), result)
            assertEquals(
                listOf<Throwable>(expectedException),
                fatalErrorUseCase.reportedThrowables,
            )
        }

    @Test
    fun getBookshelfImageCacheInfo_whenAllBookshelfReturnsError_reportsFatalErrorAndEmitsError() =
        runTest {
            val fatalErrorUseCase = StubSendFatalErrorUseCase()
            val expectedException = IllegalStateException("Repository failure")
            val useCase = GetBookshelfImageCacheInfoUseCaseImpl(
                bookshelfRepository = object : StubBookshelfRepository() {
                    override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                        Resource.Error(Resource.SystemError(expectedException))
                },
                imageCacheRepository = StubImageCacheRepository(),
                sendFatalErrorUseCase = fatalErrorUseCase,
            )

            val result = useCase().first()

            assertEquals(Resource.Error(Unit), result)
            assertEquals(
                listOf<Throwable>(expectedException),
                fatalErrorUseCase.reportedThrowables,
            )
        }

    @Test
    fun getBookshelfImageCacheInfo_whenAllBookshelfFlowThrows_reportsFatalErrorAndEmitsError() =
        runTest {
            val fatalErrorUseCase = StubSendFatalErrorUseCase()
            val expectedException = IllegalStateException("Flow failure")
            val useCase = GetBookshelfImageCacheInfoUseCaseImpl(
                bookshelfRepository = object : StubBookshelfRepository() {
                    override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                        Resource.Success(
                            flow {
                                throw expectedException
                            },
                        )
                },
                imageCacheRepository = StubImageCacheRepository(),
                sendFatalErrorUseCase = fatalErrorUseCase,
            )

            val result = useCase().first()

            assertEquals(Resource.Error(Unit), result)
            assertEquals(
                listOf<Throwable>(expectedException),
                fatalErrorUseCase.reportedThrowables,
            )
        }

    @Test
    fun getBookshelfImageCacheInfo_whenCancelled_rethrowsCancellationException() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val useCase = GetBookshelfImageCacheInfoUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> =
                    Resource.Success(
                        flow {
                            throw CancellationException("Cancelled")
                        },
                    )
            },
            imageCacheRepository = StubImageCacheRepository(),
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        assertFailsWith<CancellationException> {
            useCase().first()
        }
        assertEquals(0, fatalErrorUseCase.reportedThrowables.size)
    }

    @Test
    fun getOtherImageCacheInfo_whenSuccess_returnsOtherImageCache() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val otherCache = OtherImageCache(size = 500L, maxSize = 5000L)
        val useCase = GetOtherImageCacheInfoUseCaseImpl(
            imageCacheRepository = object : StubImageCacheRepository() {
                override fun getOtherImageCache(): Resource<OtherImageCache, Resource.SystemError> =
                    Resource.Success(otherCache)
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        val result = useCase()

        assertEquals(Resource.Success(otherCache), result)
        assertEquals(0, fatalErrorUseCase.reportedThrowables.size)
    }

    @Test
    fun getOtherImageCacheInfo_whenRepositoryReturnsError_reportsFatalErrorAndReturnsError() =
        runTest {
            val fatalErrorUseCase = StubSendFatalErrorUseCase()
            val expectedException = RuntimeException("Disk read failure")
            val useCase = GetOtherImageCacheInfoUseCaseImpl(
                imageCacheRepository = object : StubImageCacheRepository() {
                    override fun getOtherImageCache(): Resource<OtherImageCache, Resource.SystemError> =
                        Resource.Error(Resource.SystemError(expectedException))
                },
                sendFatalErrorUseCase = fatalErrorUseCase,
            )

            val result = useCase()

            assertEquals(Resource.Error(Unit), result)
            assertEquals(
                listOf<Throwable>(expectedException),
                fatalErrorUseCase.reportedThrowables,
            )
        }

    @Test
    fun getOtherImageCacheInfo_whenCancelled_rethrowsCancellationException() = runTest {
        val fatalErrorUseCase = StubSendFatalErrorUseCase()
        val useCase = GetOtherImageCacheInfoUseCaseImpl(
            imageCacheRepository = object : StubImageCacheRepository() {
                override fun getOtherImageCache(): Resource<OtherImageCache, Resource.SystemError> =
                    throw CancellationException("Cancelled")
            },
            sendFatalErrorUseCase = fatalErrorUseCase,
        )

        assertFailsWith<CancellationException> {
            useCase()
        }
        assertEquals(0, fatalErrorUseCase.reportedThrowables.size)
    }
}
