/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.wrapper

import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.scaleToBounds
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.feature.book.BookScreenUiState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughOut

@Composable
internal fun BookScreenWrapper(
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
    with(LocalSharedTransitionScope.current) {
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
                    content(uiState)
                }
            }
        }
    }
}
