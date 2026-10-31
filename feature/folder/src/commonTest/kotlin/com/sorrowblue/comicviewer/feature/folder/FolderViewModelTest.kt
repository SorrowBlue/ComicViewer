/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.folder

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderSortScope
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.model.settings.folder.isSameFolderPath
import com.sorrowblue.comicviewer.domain.model.settings.folder.isSubfolderPath
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class, InternalDataApi::class)
class FolderViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val bookshelfId = BookshelfId(1)
    private val folderPath = "/manga"
    private val subfolderPath = "/manga/sub"

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_reflectsCurrentSortScopeAndSortTypeAcrossAllScopes() = runTest(testDispatcher) {
        val fakeSettingsUseCase = FakeManageFolderDisplaySettingsUseCase(
            FolderDisplaySettings(sortType = SortType.Name(true)),
        )
        val viewModel = createViewModel(
            path = folderPath,
            settingsUseCase = fakeSettingsUseCase,
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // Initially Global + Name(true)
        assertEquals(
            FolderSortScope.Global,
            viewModel.uiState.value.folderAppBarUiState.sortScope,
        )
        assertEquals(SortType.Name(true), viewModel.uiState.value.folderAppBarUiState.sortType)

        // Switch to FolderOnly + Date(false)
        viewModel.onSortScopeClick(FolderSortScope.FolderOnly)
        viewModel.onSortClick(SortType.Date(false))
        advanceUntilIdle()

        assertEquals(
            FolderSortScope.FolderOnly,
            viewModel.uiState.value.folderAppBarUiState.sortScope,
        )
        assertEquals(SortType.Date(false), viewModel.uiState.value.folderAppBarUiState.sortType)

        // Switch to Subfolders
        viewModel.onSortScopeClick(FolderSortScope.Subfolders)
        advanceUntilIdle()

        assertEquals(
            FolderSortScope.Subfolders,
            viewModel.uiState.value.folderAppBarUiState.sortScope,
        )
        assertEquals(SortType.Date(false), viewModel.uiState.value.folderAppBarUiState.sortType)
    }

    @Test
    fun onSortClick_emitsReloadOnlyWhenEffectiveSortTypeChanges() = runTest(testDispatcher) {
        val fakeSettingsUseCase = FakeManageFolderDisplaySettingsUseCase(
            FolderDisplaySettings(sortType = SortType.Name(true)),
        )
        val viewModel = createViewModel(
            path = folderPath,
            settingsUseCase = fakeSettingsUseCase,
        )
        val emittedEvents = mutableListOf<FolderScreenUiEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { emittedEvents.add(it) }
        }
        advanceUntilIdle()

        // Selecting the same sort type does not emit Reload
        viewModel.onSortClick(SortType.Name(true))
        advanceUntilIdle()
        assertTrue(emittedEvents.isEmpty())

        // Selecting a different sort type emits Reload
        viewModel.onSortClick(SortType.Size(false))
        advanceUntilIdle()
        assertEquals(listOf<FolderScreenUiEvent>(FolderScreenUiEvent.Reload), emittedEvents)
    }

    @Test
    fun onSortScopeClick_emitsReloadWhenScopeSwitchChangesEffectiveSortType() =
        runTest(testDispatcher) {
            // Regression test for Bug 2:
            // Global is Name(true), /manga has FolderOnly(Date(false)).
            // Switching /manga from FolderOnly to Global changes effective sort from Date(false) to Name(true),
            // which MUST emit FolderScreenUiEvent.Reload.
            val fakeSettingsUseCase = FakeManageFolderDisplaySettingsUseCase(
                FolderDisplaySettings(
                    sortType = SortType.Name(true),
                    folderScopeOnlyList = listOf(
                        FolderScopeOnly(
                            bookshelfId = bookshelfId,
                            path = folderPath,
                            sortType = SortType.Date(false),
                            includeSubfolders = false,
                        ),
                    ),
                ),
            )
            val viewModel = createViewModel(
                path = folderPath,
                settingsUseCase = fakeSettingsUseCase,
            )
            val emittedEvents = mutableListOf<FolderScreenUiEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.events.collect { emittedEvents.add(it) }
            }
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.collect {}
            }
            advanceUntilIdle()

            assertEquals(SortType.Date(false), viewModel.uiState.value.folderAppBarUiState.sortType)

            // Switch from FolderOnly(Date(false)) -> Global(Name(true))
            viewModel.onSortScopeClick(FolderSortScope.Global)
            advanceUntilIdle()

            assertEquals(listOf<FolderScreenUiEvent>(FolderScreenUiEvent.Reload), emittedEvents)
            assertEquals(
                FolderSortScope.Global,
                viewModel.uiState.value.folderAppBarUiState.sortScope,
            )
            assertEquals(
                SortType.Name(true),
                viewModel.uiState.value.folderAppBarUiState.sortType,
            )
        }

    @Test
    fun subfolderViewModel_inheritsSubfoldersAndCanOptIntoGlobalOrOverride() =
        runTest(testDispatcher) {
            val fakeSettingsUseCase = FakeManageFolderDisplaySettingsUseCase(
                FolderDisplaySettings(
                    sortType = SortType.Name(true),
                    folderScopeOnlyList = listOf(
                        FolderScopeOnly(
                            bookshelfId = bookshelfId,
                            path = folderPath,
                            sortType = SortType.Date(false),
                            includeSubfolders = true,
                        ),
                    ),
                ),
            )
            val subViewModel = createViewModel(
                path = subfolderPath,
                settingsUseCase = fakeSettingsUseCase,
            )
            val emittedEvents = mutableListOf<FolderScreenUiEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                subViewModel.events.collect { emittedEvents.add(it) }
            }
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                subViewModel.uiState.collect {}
            }
            advanceUntilIdle()

            // Subfolder initially inherits Subfolders(Date(false)) from parent
            assertEquals(
                FolderSortScope.Subfolders,
                subViewModel.uiState.value.folderAppBarUiState.sortScope,
            )
            assertEquals(
                SortType.Date(false),
                subViewModel.uiState.value.folderAppBarUiState.sortType,
            )

            // Subfolder switches to Global -> sortType changes to Name(true) and emits Reload
            subViewModel.onSortScopeClick(FolderSortScope.Global)
            advanceUntilIdle()

            assertEquals(
                FolderSortScope.Global,
                subViewModel.uiState.value.folderAppBarUiState.sortScope,
            )
            assertEquals(
                SortType.Name(true),
                subViewModel.uiState.value.folderAppBarUiState.sortType,
            )
            assertEquals(1, emittedEvents.size)
        }

    @Test
    fun onHiddenFilesChange_emitsReloadOnlyWhenChanged() = runTest(testDispatcher) {
        val fakeSettingsUseCase = FakeManageFolderDisplaySettingsUseCase(
            FolderDisplaySettings(showHiddenFiles = false),
        )
        val viewModel = createViewModel(
            path = folderPath,
            settingsUseCase = fakeSettingsUseCase,
        )
        val emittedEvents = mutableListOf<FolderScreenUiEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { emittedEvents.add(it) }
        }
        advanceUntilIdle()

        // Same value (false) -> no Reload
        viewModel.onHiddenFilesChange(false)
        advanceUntilIdle()
        assertTrue(emittedEvents.isEmpty())

        // Changed to true -> emits Reload
        viewModel.onHiddenFilesChange(true)
        advanceUntilIdle()
        assertEquals(listOf<FolderScreenUiEvent>(FolderScreenUiEvent.Reload), emittedEvents)
    }

    private fun TestScope.createViewModel(
        path: String,
        settingsUseCase: ManageFolderDisplaySettingsUseCase,
    ): FolderViewModel {
        val fakeBookshelfInfoUseCase = object : GetBookshelfInfoUseCase() {
            override fun run(request: BookshelfId): Flow<Resource<BookshelfFolder, Error>> =
                emptyFlow()
        }
        val fakePagingFileUseCase = PagingFileUseCase { flowOf(PagingData.empty()) }
        val fakeGetFileUseCase = object : GetFileUseCase() {
            override fun run(request: Request): Flow<Resource<File, Error>> = flowOf(
                Resource.Success(
                    Folder(
                        bookshelfId = request.bookshelfId,
                        name = request.path.substringAfterLast('/'),
                        parent = request.path.substringBeforeLast('/'),
                        path = request.path,
                        size = 0,
                        lastModifier = 0,
                        isHidden = false,
                    ),
                ),
            )
        }
        return FolderViewModel(
            bookshelfId = bookshelfId,
            path = path,
            restorePath = null,
            showSearch = false,
            getBookshelfInfoUseCase = fakeBookshelfInfoUseCase,
            pagingFileUseCase = fakePagingFileUseCase,
            getFileUseCase = fakeGetFileUseCase,
            folderDisplaySettingsUseCase = settingsUseCase,
        )
    }
}

private class FakeManageFolderDisplaySettingsUseCase(
    initial: FolderDisplaySettings = FolderDisplaySettings(),
) : ManageFolderDisplaySettingsUseCase {

    private val state = MutableStateFlow(initial)
    override val settings: Flow<FolderDisplaySettings> = state

    override suspend fun edit(action: (FolderDisplaySettings) -> FolderDisplaySettings) {
        state.value = action(state.value)
    }

    override suspend fun updateSortType(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Boolean {
        val current = state.value
        val previousSortType = current.currentSortType(bookshelfId, path)
        val scope = current.currentSortScope(bookshelfId, path)
        val newSettings = when (scope) {
            FolderSortScope.Global -> current.copy(sortType = sortType)

            FolderSortScope.FolderOnly -> {
                val filtered = current.folderScopeOnlyList.filterNot {
                    it.bookshelfId == bookshelfId && isSameFolderPath(it.path, path)
                }
                current.copy(
                    folderScopeOnlyList = filtered + FolderScopeOnly(
                        bookshelfId = bookshelfId,
                        path = path,
                        sortType = sortType,
                        includeSubfolders = false,
                    ),
                )
            }

            FolderSortScope.Subfolders -> {
                val filtered = current.folderScopeOnlyList.filterNot {
                    it.bookshelfId == bookshelfId &&
                        (isSameFolderPath(it.path, path) || isSubfolderPath(path, it.path))
                }
                current.copy(
                    folderScopeOnlyList = filtered + FolderScopeOnly(
                        bookshelfId = bookshelfId,
                        path = path,
                        sortType = sortType,
                        includeSubfolders = true,
                    ),
                )
            }
        }
        state.value = newSettings
        return newSettings.currentSortType(bookshelfId, path) != previousSortType
    }

    override suspend fun updateSortScope(
        bookshelfId: BookshelfId,
        path: String,
        scope: FolderSortScope,
    ): Boolean {
        val current = state.value
        val previousSortType = current.currentSortType(bookshelfId, path)
        val filtered = current.folderScopeOnlyList.filterNot {
            it.bookshelfId == bookshelfId && isSameFolderPath(it.path, path)
        }
        val newList = when (scope) {
            FolderSortScope.Global -> {
                val hasSubfolderAncestor = filtered.any {
                    it.bookshelfId == bookshelfId &&
                        !it.useGlobal &&
                        it.includeSubfolders &&
                        isSubfolderPath(it.path, path)
                }
                if (hasSubfolderAncestor) {
                    filtered + FolderScopeOnly(
                        bookshelfId = bookshelfId,
                        path = path,
                        sortType = current.sortType,
                        includeSubfolders = false,
                        useGlobal = true,
                    )
                } else {
                    filtered
                }
            }

            FolderSortScope.FolderOnly -> filtered + FolderScopeOnly(
                bookshelfId = bookshelfId,
                path = path,
                sortType = previousSortType,
                includeSubfolders = false,
            )

            FolderSortScope.Subfolders -> {
                val withoutDescendants = filtered.filterNot {
                    it.bookshelfId == bookshelfId && isSubfolderPath(path, it.path)
                }
                withoutDescendants + FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = path,
                    sortType = previousSortType,
                    includeSubfolders = true,
                )
            }
        }
        val newSettings = current.copy(folderScopeOnlyList = newList)
        state.value = newSettings
        return newSettings.currentSortType(bookshelfId, path) != previousSortType
    }
}
