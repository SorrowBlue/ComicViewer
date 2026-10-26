/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.usecase.StubBookshelfRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class FlowBookshelfUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val testBookshelf = DeviceStorage(bookshelfId, "TestShelf", 0, false)

    @Test
    fun testFlowBookshelfUseCase_success() = runTest {
        val fakeBookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(testBookshelf)
        }

        val useCase = FlowBookshelfUseCaseImpl(fakeBookshelfRepository)
        val result = useCase(bookshelfId).first()

        assertTrue(result is Resource.Success)
        assertEquals(testBookshelf, result.data)
    }

    @Test
    fun testFlowBookshelfUseCase_notFound() = runTest {
        val fakeBookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(null)
        }

        val useCase = FlowBookshelfUseCaseImpl(fakeBookshelfRepository)
        val result = useCase(bookshelfId).first()

        assertTrue(result is Resource.Success)
        assertEquals(null, result.data)
    }

    @Test
    fun testFlowBookshelfUseCase_whenFlowThrowsException_emitsSystemError() = runTest {
        val fakeBookshelfRepository = object : StubBookshelfRepository() {
            override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flow {
                throw IllegalStateException("Database failure")
            }
        }

        val useCase = FlowBookshelfUseCaseImpl(fakeBookshelfRepository)
        val result = useCase(bookshelfId).first()

        assertEquals(Resource.Error(FlowBookshelfUseCase.Error.System), result)
    }
}
