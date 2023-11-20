package com.sorrowblue.comicviewer.feature.book

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.settings.History
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.feature.book.navigation.BookArgs
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.BookPagerUiState
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logcat.logcat

@Stable
internal class BookScreenState(
    args: BookArgs,
    val scope: CoroutineScope,
    val systemUiController: SystemUiController,
    val viewModel: BookViewModel,
    uiState: BookScreenUiState = BookScreenUiState.Loading(args.name),
) {

    var uiState by mutableStateOf(uiState)
        private set

    val currentList = mutableStateListOf<BookPage>()

    init {
        scope.launch {
            val book = viewModel.getBook(args.bookshelfId, args.path)
            if (book == null) {
                this@BookScreenState.uiState = BookScreenUiState.Error(args.name)
                return@launch
            }
            val bookPagerUiState = BookPagerUiState(
                book = book,
                nextBook = viewModel.nextBook(GetNextComicRel.NEXT),
                prevBook = viewModel.nextBook(GetNextComicRel.PREV),
            )
            if (book.totalPageCount <= 0) {
                this@BookScreenState.uiState = BookScreenUiState.Error(args.name)
            } else {
                this@BookScreenState.uiState = BookScreenUiState.Loaded(
                    book,
                    bookPagerUiState,
                    true
                )
                currentList.addAll(getBookPageList(book.totalPageCount))

            }
            viewModel.updateHistory(
                History(
                    book.bookshelfId,
                    book.parent,
                    args.position
                )
            )
        }
    }

    fun toggleTooltip() {
        if (uiState is BookScreenUiState.Loaded) {
            uiState =
                (uiState as BookScreenUiState.Loaded).copy(isVisibleTooltip = !systemUiController.isSystemBarsVisible)
        }
        systemUiController.isSystemBarsVisible = !systemUiController.isSystemBarsVisible
    }

    fun onScreenDispose() {
        systemUiController.isSystemBarsVisible = true
    }

    suspend fun save(index: Int) {
        logcat("Compose") { "start save history" }
        measureTimeMillis {
            viewModel.updateLastReadPage(index)
        }.also {
            logcat { "updateLastReadPage $it" }
        }
    }
}

@Composable
internal fun rememberBookScreenState(
    args: BookArgs,
    scope: CoroutineScope = rememberCoroutineScope(),
    systemUiController: SystemUiController = rememberSystemUiController(),
    viewModel: BookViewModel = hiltViewModel(),
) = remember {
    BookScreenState(
        args = args,
        scope = scope,
        systemUiController = systemUiController,
        viewModel = viewModel
    )
}
