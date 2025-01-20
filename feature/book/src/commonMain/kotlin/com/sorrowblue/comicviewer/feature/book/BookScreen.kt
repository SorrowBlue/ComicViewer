package com.sorrowblue.comicviewer.feature.book

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil3.Bitmap
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.book.section.BookAppBar
import com.sorrowblue.comicviewer.feature.book.section.BookBottomBar
import com.sorrowblue.comicviewer.feature.book.section.BookSheet
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.feature.book.section.PageItem
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import com.sorrowblue.comicviewer.framework.annotation.Destination
import kotlinx.serialization.Serializable
import com.sorrowblue.comicviewer.domain.model.file.Book as BookFile

internal sealed interface BookScreenUiState {

    data class Loading(val name: String) : BookScreenUiState

    data class Error(val name: String) : BookScreenUiState

    data class Loaded(
        val book: BookFile,
        val favoriteId: FavoriteId,
        val bookSheetUiState: BookSheetUiState,
        val isVisibleTooltip: Boolean = true,
    ) : BookScreenUiState
}

interface BookScreenNavigator {
    fun navigateUp()
    fun onSettingsClick()
    fun onNextBookClick(book: BookFile, favoriteId: FavoriteId)
    fun onContainerLongClick()
}

@Serializable
data class Book(
    val bookshelfId: BookshelfId,
    val path: String,
    val name: String,
    val favoriteId: FavoriteId = FavoriteId(),
)

@Destination<Book>
@Composable
internal fun BookScreen(route: Book, navigator: BookScreenNavigator) {
    BookScreen(
        route = route,
        onBackClick = navigator::navigateUp,
        onSettingsClick = navigator::onSettingsClick,
        onNextBookClick = navigator::onNextBookClick,
        onContainerLongClick = navigator::onContainerLongClick,
    )
}

@Composable
private fun BookScreen(
    route: Book,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNextBookClick: (BookFile, FavoriteId) -> Unit,
    onContainerLongClick: () -> Unit,
    loadingState: BookLoadingScreenState = rememberBookLoadingScreenState(route = route),
) {
    when (val uiState = loadingState.uiState) {
        is BookScreenUiState.Loading ->
            BookLoadingScreen(uiState = uiState, onBackClick = onBackClick)

        is BookScreenUiState.Error ->
            BookErrorScreen(uiState = uiState, onBackClick = onBackClick)

        is BookScreenUiState.Loaded -> {
            val state = rememberBookScreenState(uiState)
            BookScreen(
                uiState = state.uiState,
                pagerState = state.pagerState,
                currentList = state.currentList,
                onBackClick = onBackClick,
                onNextBookClick = { onNextBookClick(it, route.favoriteId) },
                onContainerClick = state::toggleTooltip,
                onContainerLongClick = onContainerLongClick,
                onPageChange = state::onPageChange,
                onSettingsClick = onSettingsClick,
                onPageLoad = state::onPageLoad,
            )
            DisposableEffect(Unit) {
                onDispose(state::onScreenDispose)
            }
            LifecycleEventEffect(event = Lifecycle.Event.ON_PAUSE, onEvent = state::onStop)
        }
    }
}

@Composable
internal fun BookScreen(
    uiState: BookScreenUiState.Loaded,
    pagerState: PagerState,
    currentList: SnapshotStateList<PageItem>,
    onBackClick: () -> Unit,
    onNextBookClick: (BookFile) -> Unit,
    onContainerClick: () -> Unit,
    onContainerLongClick: () -> Unit,
    onPageChange: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onPageLoad: (UnratedPage, Bitmap) -> Unit,
) {
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = uiState.isVisibleTooltip,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it }
            ) {
                BookAppBar(
                    title = uiState.book.name,
                    onBackClick = onBackClick,
                    onSettingsClick = onSettingsClick
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.isVisibleTooltip,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                BookBottomBar(
                    pageRange = 1f..uiState.book.totalPageCount.toFloat(),
                    currentPage = pagerState.currentPage,
                    onPageChange = onPageChange
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { _ ->
        BookSheet(
            uiState = uiState.bookSheetUiState,
            pagerState = pagerState,
            pages = currentList,
            onClick = onContainerClick,
            onLongClick = onContainerLongClick,
            onNextBookClick = onNextBookClick,
            onPageLoad = onPageLoad
        )
    }
}
