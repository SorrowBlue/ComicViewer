package com.sorrowblue.comicviewer.feature.book

import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.scaleToBounds
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sorrowblue.comicviewer.domain.model.PluginManager
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book as BookFile
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons.Plugin
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
        }
    }
}
