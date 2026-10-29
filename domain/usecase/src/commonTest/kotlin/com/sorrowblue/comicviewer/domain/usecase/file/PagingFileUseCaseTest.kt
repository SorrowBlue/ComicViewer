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
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.StubBookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.StubFileRepository
import com.sorrowblue.comicviewer.domain.usecase.StubSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun pagingFile_whenFlowEmitsNull_returnsEmptyFlow() = runTest {
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
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> =
                    flowOf(null)
            },
            settingsRepository = StubSettingsRepository(),
        )

        val items = useCase(
            PagingFileUseCase.Request(PagingConfig(20), bookshelfId, "/missing"),
        ).toList()

        assertTrue(items.isEmpty())
    }

    @Test
    fun pagingFile_whenFlowEmitsBookFile_returnsEmptyFlow() = runTest {
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
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> =
                    flowOf(bookFile)
            },
            settingsRepository = StubSettingsRepository(),
        )

        val items = useCase(
            PagingFileUseCase.Request(PagingConfig(20), bookshelfId, bookFile.path),
        ).toList()

        assertTrue(items.isEmpty())
    }

    @Test
    fun pagingFile_whenFlowThrowsException_returnsEmptyFlow() = runTest {
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
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> = flow {
                    throw IllegalStateException("DB error")
                }
            },
            settingsRepository = StubSettingsRepository(),
        )

        val items = useCase(
            PagingFileUseCase.Request(PagingConfig(20), bookshelfId, "/manga"),
        ).toList()

        assertTrue(items.isEmpty())
    }

    @Test
    fun pagingFile_whenFolderAddedLater_recoversAndStartsPagingFlow() = runTest {
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
        val fileState = MutableStateFlow<File?>(null)
        var innerPagingActive = false

        val useCase = PagingFileUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            fileRepository = object : StubFileRepository() {
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> = fileState
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

        assertFalse(innerPagingActive)

        fileState.value = folder
        assertTrue(innerPagingActive)

        job.cancel()
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
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> =
                    flowOf(folder)

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
    fun pagingFolderBookThumbnails_whenFlowThrowsException_returnsEmptyFlow() = runTest {
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
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> = flow {
                    throw IllegalStateException("DB error")
                }
            },
            settingsRepository = StubSettingsRepository(),
        )

        val items = useCase(
            PagingFolderBookThumbnailsUseCase.Request(bookshelfId, "/manga", PagingConfig(20)),
        ).toList()

        assertTrue(items.isEmpty())
    }

    @Test
    fun pagingFile_whenUiOnlySettingsChange_doesNotRecreatePagingFlow() = runTest {
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
        val settingsState = MutableStateFlow(FolderDisplaySettings(sortType = SortType.Name(true)))
        var pagingFlowCreationCount = 0

        val useCase = PagingFileUseCaseImpl(
            bookshelfRepository = object : StubBookshelfRepository() {
                override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = flowOf(bookshelf)
            },
            fileRepository = object : StubFileRepository() {
                override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> =
                    flowOf(folder)

                override fun pagingDataFlow(
                    pagingConfig: PagingConfig,
                    bookshelf: Bookshelf,
                    file: File,
                    searchCondition: () -> SearchCondition,
                ): Flow<PagingData<File>> = flow {
                    pagingFlowCreationCount++
                    emit(PagingData.empty())
                    awaitCancellation()
                }
            },
            settingsRepository = object : StubSettingsRepository() {
                override val folderDisplaySettings: Flow<FolderDisplaySettings> = settingsState
            },
        )

        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            useCase(
                PagingFileUseCase.Request(PagingConfig(20), bookshelfId, folder.path),
            ).collect {}
        }

        assertEquals(1, pagingFlowCreationCount)

        // Changing UI-only properties should NOT recreate the PagingData flow
        settingsState.value = settingsState.value.copy(
            fileListDisplay = FileListDisplay.List,
            gridColumnSize = GridColumnSize.Large,
        )
        assertEquals(1, pagingFlowCreationCount)

        // Changing effective sortType SHOULD recreate the PagingData flow
        settingsState.value = settingsState.value.copy(sortType = SortType.Date(false))
        assertEquals(2, pagingFlowCreationCount)

        job.cancel()
    }
}
