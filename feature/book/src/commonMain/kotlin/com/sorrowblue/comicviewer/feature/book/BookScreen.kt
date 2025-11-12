package com.sorrowblue.comicviewer.feature.book

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.scaleToBounds
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.Bitmap
import com.sorrowblue.comicviewer.domain.model.PluginManager
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book as BookFile
import com.sorrowblue.comicviewer.feature.book.section.BookAppBar
import com.sorrowblue.comicviewer.feature.book.section.BookBottomBar
import com.sorrowblue.comicviewer.feature.book.section.BookSheet
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.feature.book.section.PageItem
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons.Plugin
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughOut

@Composable
context(context: BookScreenContext)
fun BookScreen(
    bookshelfId: BookshelfId,
    path: String,
    name: String,
    collectionId: CollectionId,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNextBookClick: (BookFile, CollectionId) -> Unit,
    onContainerLongClick: () -> Unit,
) {
    val prepareScreenState = rememberBookPrepareScreenState(
        bookshelfId = bookshelfId,
        path = path,
        name = name,
        collectionId = collectionId,
    )
    var error by remember { mutableStateOf("") }
    DisposableEffect(Unit) {
        val callback = object : PluginManager.Callback {
            override fun onError(msg: String) {
                error = msg
            }
        }
        context.pluginManager.addCallback(callback)
        onDispose {
            context.pluginManager.removeCallback(callback)
        }
    }

    if (error.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Icon(
                modifier = Modifier.size(96.dp),
                painter = rememberVectorPainter(image = ComicIcons.Plugin),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
            Text(
                text = error,
                style = ComicTheme.typography.bodyLarge,
            )
        }
        return
    }

    with(LocalAppState.current) {
        val boundsTransform = ComicTheme.motionScheme.slowSpatialSpec<Rect>()
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .sharedBounds(
                    rememberSharedContentState("$bookshelfId:$path"),
                    LocalNavAnimatedContentScope.current,
                    enter = materialFadeThroughIn(),
                    exit = materialFadeThroughOut(),
                    boundsTransform = { _, _ -> boundsTransform },
                    resizeMode = scaleToBounds(ContentScale.Fit, Center),
                ),
        ) {
            when (val uiState = prepareScreenState.uiState) {
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
                        onNextBookClick = { onNextBookClick(it, collectionId) },
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
    }
}

internal sealed interface BookScreenUiState {
    data class Loading(val name: String) : BookScreenUiState

    data class Error(val name: String) : BookScreenUiState

    data class Loaded(
        val book: BookFile,
        val collectionId: CollectionId,
        val bookSheetUiState: BookSheetUiState,
        val isVisibleTooltip: Boolean = true,
    ) : BookScreenUiState
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
                exit = slideOutVertically { -it },
            ) {
                BookAppBar(
                    title = uiState.book.name,
                    onBackClick = onBackClick,
                    onSettingsClick = onSettingsClick,
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.isVisibleTooltip,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                BookBottomBar(
                    pageRange = 1f..uiState.book.totalPageCount.toFloat(),
                    currentPage = pagerState.currentPage,
                    onPageChange = onPageChange,
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { _ ->
        BookSheet(
            uiState = uiState.bookSheetUiState,
            pagerState = pagerState,
            pages = currentList,
            onClick = onContainerClick,
            onLongClick = onContainerLongClick,
            onNextBookClick = onNextBookClick,
            onPageLoad = onPageLoad,
        )
    }
}
