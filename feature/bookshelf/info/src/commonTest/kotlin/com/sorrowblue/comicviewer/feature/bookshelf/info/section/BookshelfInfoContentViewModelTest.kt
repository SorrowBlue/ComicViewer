/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class BookshelfInfoContentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var scanManager: FakeBookshelfScanManager
    private lateinit var viewModel: BookshelfInfoContentViewModel

    @OptIn(InternalDataApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        scanManager = FakeBookshelfScanManager()
        val bookshelfFolder = BookshelfFolder(
            bookshelf = ShareContents,
            folder = Folder(
                bookshelfId = ShareContents.id,
                name = "test",
                parent = "",
                path = "/test",
                size = 0,
                lastModifier = 0,
                isHidden = false,
            ),
        )
        val pagingUseCase = object : PagingBookshelfBookUseCase() {
            override fun run(request: Request): Flow<PagingData<BookThumbnail>> =
                flowOf(PagingData.empty())
        }
        viewModel = BookshelfInfoContentViewModel(
            bookshelfFolder = bookshelfFolder,
            pagingBookshelfBookUseCase = pagingUseCase,
            scanManager = scanManager,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun isScanningFile_initiallyFalse() {
        assertFalse(viewModel.isScanningFile.value)
    }

    @Test
    fun isScanningThumbnail_initiallyFalse() {
        assertFalse(viewModel.isScanningThumbnail.value)
    }

    @Test
    fun scanFile_callsScanManager() {
        viewModel.scanFile()
        assertTrue(scanManager.scanFileCalled)
    }

    @Test
    fun scanThumbnail_callsScanManager() {
        viewModel.scanThumbnail()
        assertTrue(scanManager.scanThumbnailCalled)
    }
}
