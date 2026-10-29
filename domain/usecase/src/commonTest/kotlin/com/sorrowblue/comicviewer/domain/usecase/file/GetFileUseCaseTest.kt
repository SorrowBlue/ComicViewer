/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.StubFileRepository
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
class GetFileUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val path = "/manga/vol1.cbz"
    private val bookFile = BookFile(
        bookshelfId = bookshelfId,
        name = "vol1.cbz",
        parent = "/manga",
        path = path,
        size = 1024L,
        lastModifier = 100L,
        isHidden = false,
    )

    @Test
    fun getFile_whenFlowEmitsFile_emitsSuccess() = runTest {
        val useCase = GetFileUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> =
                    flowOf(bookFile)
            },
        )

        val result = useCase(GetFileUseCase.Request(bookshelfId, path)).first()

        assertEquals(Resource.Success(bookFile), result)
    }

    @Test
    fun getFile_whenFlowEmitsNull_emitsNotFoundError() = runTest {
        val useCase = GetFileUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> =
                    flowOf(null)
            },
        )

        val result = useCase(GetFileUseCase.Request(bookshelfId, path)).first()

        assertEquals(Resource.Error(GetFileUseCase.Error.NotFound), result)
    }

    @Test
    fun getFile_whenFlowThrowsException_emitsReportedSystemError() = runTest {
        val useCase = GetFileUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> = flow {
                    throw IllegalStateException("Database error")
                }
            },
        )

        val result = useCase(GetFileUseCase.Request(bookshelfId, path)).first()

        assertEquals(Resource.Error(GetFileUseCase.Error.ReportedSystemError), result)
    }

    @Test
    fun getFile_whenFlowThrowsCancellationException_rethrowsCancellationException() = runTest {
        val useCase = GetFileUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> = flow {
                    throw CancellationException("Cancelled")
                }
            },
        )

        assertFailsWith<CancellationException> {
            useCase(GetFileUseCase.Request(bookshelfId, path)).first()
        }
    }

    @Test
    fun getFile_whenFlowEmitsNullThenFile_emitsNotFoundThenSuccess() = runTest {
        val useCase = GetFileUseCaseImpl(
            fileRepository = object : StubFileRepository() {
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> =
                    flowOf(null, bookFile)
            },
        )

        val results = useCase(GetFileUseCase.Request(bookshelfId, path)).toList()

        assertEquals(
            listOf(
                Resource.Error(GetFileUseCase.Error.NotFound),
                Resource.Success(bookFile),
            ),
            results,
        )
    }
}
