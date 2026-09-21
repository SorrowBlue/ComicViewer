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
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class FlowBookshelfUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val testBookshelf = DeviceStorage(bookshelfId, "TestShelf", 0, false)

    @Test
    fun testFlowBookshelfUseCase_success() = runTest {
        val fakeBookshelfRepository = object : TestBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }

        val useCase = FlowBookshelfUseCaseImpl(fakeBookshelfRepository)
        val result = useCase(bookshelfId).first()

        assertTrue(result is Resource.Success)
        assertEquals(testBookshelf, result.data)
    }

    @Test
    fun testFlowBookshelfUseCase_notFound() = runTest {
        val fakeBookshelfRepository = object : TestBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(null)
        }

        val useCase = FlowBookshelfUseCaseImpl(fakeBookshelfRepository)
        val result = useCase(bookshelfId).first()

        assertTrue(result is Resource.Success)
        assertEquals(null, result.data)
    }
}

private open class TestBookshelfRepository : BookshelfRepository {
    override suspend fun updateOrCreate(
        bookshelf: Bookshelf,
        transaction: suspend (Bookshelf) -> Unit,
    ): Bookshelf? = TODO()

    override suspend fun delete(bookshelfId: BookshelfId): Resource<Unit, Resource.SystemError> =
        TODO()

    override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = TODO()
    override fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>> =
        TODO()

    override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> = TODO()
    override suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean) = TODO()
}
