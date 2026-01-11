package com.sorrowblue.comicviewer.feature.book

import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.scaleToBounds
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book as BookFile
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughOut

@Composable
context(context: BookScreenContext)
internal fun BookScreenRoot(
    bookshelfId: BookshelfId,
    path: String,
    name: String,
    collectionId: CollectionId,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNextBookClick: (BookFile, CollectionId) -> Unit,
    onContainerLongClick: () -> Unit,
) {
    BookScreenWrapper(
        bookshelfId = bookshelfId,
        path = path,
        name = name,
        collectionId = collectionId,
        onBackClick = onBackClick,
    ) { uiState ->
        val state = rememberBookScreenState(initialUiState = uiState)
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

@Composable
context(context: BookScreenContext)
private fun BookScreenWrapper(
    bookshelfId: BookshelfId,
    path: String,
    name: String,
    collectionId: CollectionId,
    onBackClick: () -> Unit,
    content: @Composable (BookScreenUiState.Loaded) -> Unit,
) {
    val prepareScreenState = rememberBookScreenWrapperState(
        bookshelfId = bookshelfId,
        path = path,
        name = name,
        collectionId = collectionId,
    )
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

                is BookScreenUiState.PluginError ->
                    BookPluginErrorScreen(uiState = uiState, onBackClick = onBackClick)

                is BookScreenUiState.Loaded -> {
                    content(uiState)
                }
            }
        }
    }
}
