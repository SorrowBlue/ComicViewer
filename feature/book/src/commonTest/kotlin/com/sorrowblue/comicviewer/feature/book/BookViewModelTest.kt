/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.book.NextPage
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.usecase.book.CreateInitialBookPagesUseCase
import com.sorrowblue.comicviewer.domain.usecase.book.ResolveBookPageLayoutUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.CloseBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import com.sorrowblue.comicviewer.feature.book.wrapper.BookWrapperViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class, InternalDataApi::class)
class BookViewModelTest {

    private lateinit var dispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @BeforeTest
    fun setup() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        testScope = TestScope(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `BookWrapperViewModel retains Loaded state in uiStateFlow when re-collecting after navigation`() =
        testScope.runTest {
            val bookshelfId = BookshelfId(1)
            val book = createBookFile(
                bookshelfId = bookshelfId,
                totalPageCount = 20,
                lastPageRead = 4,
            )
            val getBookUseCase = FakeGetBookUseCase(Resource.Success(book))
            val manageViewerSettingsUseCase = FakeManageViewerSettingsUseCase()

            val viewModel = BookWrapperViewModel(
                bookshelfId = bookshelfId,
                path = book.path,
                name = book.name,
                collectionId = CollectionId(),
                getBookUseCase = getBookUseCase,
                manageViewerSettingsUseCase = manageViewerSettingsUseCase,
            )

            assertIs<BookScreenUiState.Loading>(viewModel.uiStateFlow.value)

            runCurrent()

            val loadedState = assertIs<BookScreenUiState.Loaded>(viewModel.uiStateFlow.value)
            assertEquals(book, loadedState.book)
        }

    @Test
    fun `BookViewModel preserves currentList, isInitialized, and currentPagerIndex across screen recreation`() =
        testScope.runTest {
            val bookshelfId = BookshelfId(1)
            val book = createBookFile(
                bookshelfId = bookshelfId,
                totalPageCount = 10,
                lastPageRead = 0,
            )

            val viewModel = BookViewModel(
                book = book,
                collectionId = CollectionId(),
                isCompactWindowClass = true,
                ioDispatcher = dispatcher,
                manageBookSettingsUseCase = FakeManageBookSettingsUseCase(),
                manageViewerSettingsUseCase = FakeManageViewerSettingsUseCase(),
                getNextBookUseCase = FakeGetNextBookUseCase(),
                updateLastReadPageUseCase = FakeUpdateLastReadPageUseCase(),
                closeBookUseCase = FakeCloseBookUseCase(),
                createInitialBookPagesUseCase = FakeCreateInitialBookPagesUseCase(),
                resolveBookPageLayoutUseCase = FakeResolveBookPageLayoutUseCase(),
            )

            runCurrent()

            val pageItems = viewModel.pageItemListFlow.value
            assertEquals(12, pageItems.size) // prev + 10 pages + next

            // Simulate initial screen load and user scrolling to page 7 (pager index 7)
            viewModel.currentList.addAll(pageItems)
            viewModel.isInitialized = true
            viewModel.currentPagerIndex = 7

            // When returning from Settings screen, BookViewModel state must still be intact
            assertTrue(viewModel.isInitialized)
            assertEquals(7, viewModel.currentPagerIndex)
            assertEquals(pageItems, viewModel.currentList.toList())
        }

    private fun createBookFile(
        bookshelfId: BookshelfId,
        totalPageCount: Int,
        lastPageRead: Int,
    ): BookFile = BookFile(
        bookshelfId = bookshelfId,
        name = "test.cbz",
        parent = "/books",
        path = "/books/test.cbz",
        size = 1024L,
        lastModifier = 0L,
        isHidden = false,
        totalPageCount = totalPageCount,
        lastPageRead = lastPageRead,
    )

    private class FakeGetBookUseCase(private val result: Resource<Book, Error>) :
        GetBookUseCase() {
        override fun run(request: Request): Flow<Resource<Book, Error>> = flowOf(result)
    }

    private class FakeManageBookSettingsUseCase : ManageBookSettingsUseCase {
        val flow = MutableStateFlow(BookSettings())
        override val settings: Flow<BookSettings> = flow

        override suspend fun edit(action: (BookSettings) -> BookSettings) {
            flow.update(action)
        }
    }

    private class FakeManageViewerSettingsUseCase : ManageViewerSettingsUseCase {
        val flow = MutableStateFlow(ViewerSettings())
        override val settings: Flow<ViewerSettings> = flow

        override suspend fun edit(action: (ViewerSettings) -> ViewerSettings) {
            flow.update(action)
        }
    }

    private class FakeGetNextBookUseCase : GetNextBookUseCase() {
        override suspend fun run(request: Request): Resource<Book, Error> =
            Resource.Error(Error.NotFound)
    }

    private class FakeUpdateLastReadPageUseCase : UpdateLastReadPageUseCase() {
        override suspend fun run(request: Request): Resource<Unit, Unit> = Resource.Success(Unit)
    }

    private class FakeCloseBookUseCase : CloseBookUseCase() {
        override suspend fun run(request: Book): Resource<Unit, Unit> = Resource.Success(Unit)
    }

    private class FakeCreateInitialBookPagesUseCase : CreateInitialBookPagesUseCase() {
        override suspend fun run(request: Request): Resource<List<BookPage>, Unit> =
            Resource.Success((0 until request.totalPageCount).map { BookPage.Default(it) })
    }

    private class FakeResolveBookPageLayoutUseCase : ResolveBookPageLayoutUseCase() {
        override suspend fun run(request: Request): Resource<List<PageItem>, Unit> =
            Resource.Success(
                request.currentList.map { item ->
                    if (item is NextPage) item else item
                },
            )
    }
}
