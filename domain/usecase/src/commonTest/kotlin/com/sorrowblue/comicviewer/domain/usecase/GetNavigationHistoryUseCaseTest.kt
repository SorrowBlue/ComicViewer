/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.NavigationHistory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class GetNavigationHistoryUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val bookshelf = DeviceStorage(bookshelfId, "Shelf", 0, false)
    private val rootFolder = Folder(bookshelfId, "root", "", "/root", 0, 0, false)
    private val subFolder = Folder(bookshelfId, "sub", "/root", "/root/sub", 0, 0, false)
    private val book = BookFile(
        bookshelfId = bookshelfId,
        name = "book.cbz",
        parent = "/root/sub",
        path = "/root/sub/book.cbz",
        size = 100L,
        lastModifier = 0L,
        isHidden = false,
    )

    @Test
    fun getNavigationHistory_whenValidHierarchy_returnsOrderedFolderListAndBook() = runTest {
        val foldersByPath = mapOf(
            "/root" to rootFolder,
            "/root/sub" to subFolder,
        )
        val useCase = GetNavigationHistoryUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun lastHistory(): Flow<File?> = flowOf(book)
                override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? =
                    foldersByPath[path]
            },
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
        )

        val result = useCase(Unit).first()
        assertEquals(
            Resource.Success(NavigationHistory(listOf(rootFolder, subFolder), book)),
            result,
        )
        val history = (result as Resource.Success).data
        assertEquals(listOf(rootFolder, subFolder), history.folderList)
        assertEquals(book, history.book)
    }

    @Test
    fun getNavigationHistory_whenParentFolderHasCycle_returnsEmptyFolderListWithoutHanging() =
        runTest {
            val cyclicFolder = Folder(
                bookshelfId = bookshelfId,
                name = "sub",
                parent = "/root/sub",
                path = "/root/sub",
                size = 0,
                lastModifier = 0,
                isHidden = false,
            )
            val useCase = GetNavigationHistoryUseCaseImpl(
                fileRepository = object : StubFileRepository() {
                    override fun lastHistory(): Flow<File?> = flowOf(book)
                    override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? =
                        if (path == "/root/sub") cyclicFolder else null
                },
                bookshelfRepository = object : StubBookshelfRepository() {
                    override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> =
                        flowOf(bookshelf)
                },
            )

            val result = useCase(Unit).first()
            assertEquals(
                Resource.Success(NavigationHistory(emptyList(), book)),
                result,
            )
        }

    @Test
    fun getNavigationHistory_whenFindByThrowsException_returnsSystemError() = runTest {
        val useCase = GetNavigationHistoryUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun lastHistory(): Flow<File?> = flowOf(book)
                override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? =
                    throw IllegalStateException("Database failure")
            },
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
        )

        val result = useCase(Unit).first()
        assertEquals(
            Resource.Error(GetNavigationHistoryUseCase.Error.System),
            result,
        )
    }

    @Test
    fun getNavigationHistory_whenLastHistoryFlowThrows_emitsSystemError() = runTest {
        val useCase = GetNavigationHistoryUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun lastHistory(): Flow<File?> = flow {
                    throw IllegalStateException("Upstream flow failure")
                }
            },
            bookshelfRepository = StubBookshelfRepository(),
        )

        val result = useCase(Unit).first()
        assertEquals(
            Resource.Error(GetNavigationHistoryUseCase.Error.System),
            result,
        )
    }

    @Test
    fun getNavigationHistory_whenBookshelfIsDeleted_returnsSystemError() = runTest {
        val deletedBookshelf = DeviceStorage(bookshelfId, "Shelf", 0, isDeleted = true)
        val useCase = GetNavigationHistoryUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun lastHistory(): Flow<File?> = flowOf(book)
            },
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> =
                    flowOf(deletedBookshelf)
            },
        )

        val result = useCase(Unit).first()
        assertEquals(
            Resource.Error(GetNavigationHistoryUseCase.Error.System),
            result,
        )
    }
}
