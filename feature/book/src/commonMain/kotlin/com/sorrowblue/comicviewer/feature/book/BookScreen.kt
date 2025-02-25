package com.sorrowblue.comicviewer.feature.book

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
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
import com.sorrowblue.cmpdestinations.annotation.Destination
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
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
internal fun BookScreen(route: Book, navigator: BookScreenNavigator = koinInject()) {
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
        val scope = rememberCoroutineScope()
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
//                    .pointerInput(Unit) {
//                        detectTapGestures {
//                            onContainerClick()
//                        }
//                    }
//                    .combinedClickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null,
//                        onClick = onContainerClick
//                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
//                    .pointerInput(Unit) {
//                        detectTapGestures {
//                            scope.launch {
//                                pagerState.scrollToPage(pagerState.currentPage + 1)
//                            }
//                        }
//                    }
//                    .combinedClickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        scope.launch {
//                            pagerState.scrollToPage(pagerState.currentPage + 1)
//                        }
//                    }
            )
        }
    }
}

suspend fun PointerInputScope.detectTapGestures2(
    onDoubleTap: ((Offset) -> Unit)? = null,
    onLongPress: ((Offset) -> Unit)? = null,
    onPress: suspend PressGestureScope.(Offset) -> Unit = { },
    onTap: ((Offset) -> Unit)? = null,
) = coroutineScope {
    // special signal to indicate to the sending side that it shouldn't intercept and consume
    // cancel/up events as we're only require down events
    awaitEachGesture {
        awaitFirstDown()
//        val down = awaitFirstDown()
//        down.consume()
        // In some cases, coroutine cancellation of the reset job might still be processing when we
        // are already processing an up or cancel pointer event. We need to wait for the reset job
        // to cancel and complete so it can clean up properly (e.g. unlock the underlying mutex)
//        val upOrCancel: PointerInputChange?
//        val cancelOrReleaseJob: Job?

        // wait for first tap up or long press
//        upOrCancel = waitForUpOrCancellation()

//        if (upOrCancel != null) {
            // tap was successful.
//            onTap?.invoke(upOrCancel.position) // no need to check for double-tap.
//        }
    }
}
