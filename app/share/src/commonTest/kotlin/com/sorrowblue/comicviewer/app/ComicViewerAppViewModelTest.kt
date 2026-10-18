/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class, InternalDataApi::class)
class ComicViewerAppViewModelTest {

    private lateinit var testScope: TestScope
    private lateinit var fakeDisplaySettingsUseCase: FakeManageDisplaySettingsUseCase
    private lateinit var fakeGetNavigationHistoryUseCase: FakeGetNavigationHistoryUseCase

    @BeforeTest
    fun setup() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        testScope = TestScope(dispatcher)
        fakeDisplaySettingsUseCase = FakeManageDisplaySettingsUseCase()
        fakeGetNavigationHistoryUseCase = FakeGetNavigationHistoryUseCase()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `restore with nested folders emits each folder once and ignores repeated restore calls`() =
        testScope.runTest {
            val bookshelfId = BookshelfId(1)
            val folders = listOf(
                Folder(
                    bookshelfId = bookshelfId,
                    name = "root",
                    parent = "",
                    path = "/root",
                    size = 0L,
                    lastModifier = 0L,
                    isHidden = false,
                ),
                Folder(
                    bookshelfId = bookshelfId,
                    name = "sub1",
                    parent = "/root",
                    path = "/root/sub1",
                    size = 0L,
                    lastModifier = 0L,
                    isHidden = false,
                ),
                Folder(
                    bookshelfId = bookshelfId,
                    name = "sub2",
                    parent = "/root/sub1",
                    path = "/root/sub1/sub2",
                    size = 0L,
                    lastModifier = 0L,
                    isHidden = false,
                ),
            )
            val book = BookFile(
                bookshelfId = bookshelfId,
                name = "comic.cbz",
                parent = "/root/sub1/sub2",
                path = "/root/sub1/sub2/comic.cbz",
                size = 100L,
                lastModifier = 0L,
                isHidden = false,
            )
            fakeDisplaySettingsUseCase.flow.value = DisplaySettings(restoreOnLaunch = true)
            fakeGetNavigationHistoryUseCase.result = Resource.Success(
                NavigationHistory(folders to book),
            )

            val viewModel = ComicViewerAppViewModel(
                manageDisplaySettingsUseCase = fakeDisplaySettingsUseCase,
                getNavigationHistoryUseCase = fakeGetNavigationHistoryUseCase,
            )

            val emittedPaths = mutableListOf<Pair<String, String?>>()
            backgroundScope.launch {
                viewModel.restoreNavigation.collect {
                    emittedPaths.add(it.path to it.restorePath)
                }
            }
            runCurrent()

            viewModel.restore(allowNavigationRestored = true)
            runCurrent()

            assertEquals(
                listOf(
                    "/root" to null,
                    "/root/sub1" to null,
                    "/root/sub1/sub2" to "/root/sub1/sub2/comic.cbz",
                ),
                emittedPaths,
            )

            // Subsequent restore calls (e.g. after returning from background lock) must be ignored
            viewModel.restore(allowNavigationRestored = true)
            runCurrent()

            assertEquals(3, emittedPaths.size)
        }

    private class FakeManageDisplaySettingsUseCase : ManageDisplaySettingsUseCase {
        val flow = MutableStateFlow(DisplaySettings())
        override val settings: Flow<DisplaySettings> = flow

        override suspend fun edit(action: (DisplaySettings) -> DisplaySettings) {
            flow.update(action)
        }
    }

    private class FakeGetNavigationHistoryUseCase : GetNavigationHistoryUseCase() {
        var result: Resource<NavigationHistory, Error> = Resource.Error(Error.System)

        override fun run(request: Unit): Flow<Resource<NavigationHistory, Error>> = flowOf(result)
    }
}
