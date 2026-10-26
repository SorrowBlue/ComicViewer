/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.StubBookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.StubFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubSettingsRepository
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class, InternalDataApi::class)
class PagingFileUseCaseTest {

    @Test
    fun pagingFile_whenFindByReturnsNull_returnsEmptyFlow() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(
            id = bookshelfId,
            displayName = "Shelf",
            fileCount = 0,
            isDeleted = false,
        )
        val useCase = PagingFileUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            fileRepository = object : StubFileRepository() {
                override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? = null
            },
            settingsRepository = StubSettingsRepository(),
        )

        val items = useCase(
            PagingFileUseCase.Request(PagingConfig(20), bookshelfId, "/missing"),
        ).toList()

        assertTrue(items.isEmpty())
    }

    @Test
    fun pagingFile_whenFindByReturnsBookFile_returnsEmptyFlow() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(
            id = bookshelfId,
            displayName = "Shelf",
            fileCount = 0,
            isDeleted = false,
        )
        val bookFile = BookFile(
            bookshelfId = bookshelfId,
            name = "book.cbz",
            parent = "/manga",
            path = "/manga/book.cbz",
            size = 100L,
            lastModifier = 0L,
            isHidden = false,
        )
        val useCase = PagingFileUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            fileRepository = object : StubFileRepository() {
                override suspend fun findBy(bookshelfId: BookshelfId, path: String): File = bookFile
            },
            settingsRepository = StubSettingsRepository(),
        )

        val items = useCase(
            PagingFileUseCase.Request(PagingConfig(20), bookshelfId, bookFile.path),
        ).toList()

        assertTrue(items.isEmpty())
    }

    @Test
    fun pagingFile_whenFindByThrowsException_returnsEmptyFlow() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(
            id = bookshelfId,
            displayName = "Shelf",
            fileCount = 0,
            isDeleted = false,
        )
        val useCase = PagingFileUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            fileRepository = object : StubFileRepository() {
                override suspend fun findBy(bookshelfId: BookshelfId, path: String): File =
                    throw IllegalStateException("DB error")
            },
            settingsRepository = StubSettingsRepository(),
        )

        val items = useCase(
            PagingFileUseCase.Request(PagingConfig(20), bookshelfId, "/manga"),
        ).toList()

        assertTrue(items.isEmpty())
    }

    @Test
    fun pagingFile_whenBookshelfBecomesNull_cancelsActivePagingFlow() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(
            id = bookshelfId,
            displayName = "Shelf",
            fileCount = 0,
            isDeleted = false,
        )
        val folder = Folder(
            bookshelfId = bookshelfId,
            name = "manga",
            parent = "",
            path = "/manga",
            size = 0,
            lastModifier = 0,
            isHidden = false,
        )
        val bookshelfState = MutableStateFlow<Bookshelf?>(bookshelf)
        var innerPagingActive = false

        val useCase = PagingFileUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = bookshelfState
            },
            fileRepository = object : StubFileRepository() {
                override suspend fun findBy(bookshelfId: BookshelfId, path: String): File = folder
                override fun pagingDataFlow(
                    pagingConfig: PagingConfig,
                    bookshelf: Bookshelf,
                    file: File,
                    searchCondition: () -> SearchCondition,
                ): Flow<PagingData<File>> = flow {
                    innerPagingActive = true
                    try {
                        emit(PagingData.empty())
                        awaitCancellation()
                    } finally {
                        innerPagingActive = false
                    }
                }
            },
            settingsRepository = object : StubSettingsRepository() {
                override val folderDisplaySettings: Flow<FolderDisplaySettings> =
                    flowOf(FolderDisplaySettings())
            },
        )

        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            useCase(
                PagingFileUseCase.Request(PagingConfig(20), bookshelfId, folder.path),
            ).collect {}
        }

        assertTrue(innerPagingActive)

        bookshelfState.value = null
        assertFalse(innerPagingActive)

        job.cancel()
    }

    @Test
    fun pagingFolderBookThumbnails_whenFindByThrowsException_returnsEmptyFlow() = runTest {
        val bookshelfId = BookshelfId(1)
        val bookshelf = DeviceStorage(
            id = bookshelfId,
            displayName = "Shelf",
            fileCount = 0,
            isDeleted = false,
        )
        val useCase = PagingFolderBookThumbnailsUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            fileRepository = object : StubFileRepository() {
                override suspend fun findBy(bookshelfId: BookshelfId, path: String): File =
                    throw IllegalStateException("DB error")
            },
            settingsRepository = StubSettingsRepository(),
        )

        val items = useCase(
            PagingFolderBookThumbnailsUseCase.Request(bookshelfId, "/manga", PagingConfig(20)),
        ).toList()

        assertTrue(items.isEmpty())
    }
}
