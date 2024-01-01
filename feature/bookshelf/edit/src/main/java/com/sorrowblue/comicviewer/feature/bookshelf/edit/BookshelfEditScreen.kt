package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs

@Composable
internal fun BookshelfEditScreen(
    args: BookshelfEditArgs,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    contentPadding: PaddingValues,
    state: BookshelfEditScreenState = rememberNeoBookshelfEditScreenState(args),
) {
    when (val screenState = state.innerScreenState) {
        BookshelfEditLoading -> Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is StorageEditScreenState -> {
            StorageEditRoute(
                state = screenState,
                onBackClick = onBackClick,
                onComplete = onComplete,
                contentPadding = contentPadding
            )
        }

        is SmbEditScreenState -> {
            SmbEditScreen(
                state = screenState,
                onBackClick = onBackClick,
                onComplete = onComplete,
                contentPadding = contentPadding
            )
        }
    }
}
