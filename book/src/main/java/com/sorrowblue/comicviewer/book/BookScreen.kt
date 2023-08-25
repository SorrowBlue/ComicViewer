package com.sorrowblue.comicviewer.book

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.request.BookPageRequest
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel

class BookRouter(val navController: NavController)

abstract class BookScreenState {

    abstract val router: BookRouter
    abstract val totalPageCount: Int @Composable get
    abstract val book: Book? @Composable get
}

internal class BookScreenStateImpl(
    private val viewModel: BookViewModel,
    private val commonViewModel: CommonViewModel,
    override val router: BookRouter
) : BookScreenState() {
    override val totalPageCount: Int
        @Composable
        get() = viewModel.pageCount.collectAsState().value

    override val book: Book?
        @Composable get() = viewModel.bookFlow.collectAsState().value
}

@Composable
internal fun rememberBookScreenState(
    navController: NavController,
    composableBackStackEntry: NavBackStackEntry,
    viewModel: BookViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel = hiltViewModel(remember(composableBackStackEntry) {
        navController.getBackStackEntry("Parent")
    }),
): BookScreenState = remember {
    BookScreenStateImpl(viewModel, commonViewModel, BookRouter(navController))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BookScreen(state: BookScreenState) {
    val total = state.totalPageCount
    val book = state.book
    val pagerState = rememberPagerState(pageCount = { total })
    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 2,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        if (book != null) {
            AsyncImage(
                model = BookPageRequest(book to page),
                null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
