package com.sorrowblue.comicviewer.feature.book

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Parcelable
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.navigation.BookGraph
import com.sorrowblue.comicviewer.feature.book.navigation.BookGraphTransitions
import com.sorrowblue.comicviewer.feature.book.section.BookAppBar
import com.sorrowblue.comicviewer.feature.book.section.BookBottomBar
import com.sorrowblue.comicviewer.feature.book.section.BookSheet
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.feature.book.section.PageItem
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import kotlinx.parcelize.Parcelize

internal sealed interface BookScreenUiState {

    data class Loading(val name: String) : BookScreenUiState

    data class Error(val name: String) : BookScreenUiState

    data class Loaded(
        val book: Book,
        val favoriteId: FavoriteId,
        val bookSheetUiState: BookSheetUiState,
        val isVisibleTooltip: Boolean = true,
    ) : BookScreenUiState
}

interface BookScreenNavigator {
    fun navigateUp()
    fun onSettingsClick()
    fun onNextBookClick(book: Book, favoriteId: FavoriteId)
    fun onContainerLongClick()
}

@Parcelize
class BookArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val name: String,
    val favoriteId: FavoriteId = FavoriteId(),
) : Parcelable

@Destination<BookGraph>(
    start = true,
    navArgs = BookArgs::class,
    style = BookGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun BookScreen(
    args: BookArgs,
    navigator: BookScreenNavigator,
) {
    BookScreen(
        args = args,
        onBackClick = navigator::navigateUp,
        onSettingsClick = navigator::onSettingsClick,
        onNextBookClick = navigator::onNextBookClick,
        onContainerLongClick = navigator::onContainerLongClick,
    )
}

@Composable
private fun BookScreen(
    args: BookArgs,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNextBookClick: (Book, FavoriteId) -> Unit,
    onContainerLongClick: () -> Unit,
    loadingState: BookLoadingScreenState = rememberBookLoadingScreenState(args = args),
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
                onNextBookClick = { onNextBookClick(it, args.favoriteId) },
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun BookScreen(
    uiState: BookScreenUiState.Loaded,
    pagerState: PagerState,
    currentList: SnapshotStateList<PageItem>,
    onBackClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
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
