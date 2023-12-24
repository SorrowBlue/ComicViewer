package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs

@Composable
internal fun BookshelfEditRoute(
    args: BookshelfEditArgs,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    state: BookshelfEditScreenState = rememberNeoBookshelfEditScreenState(
        args,
        remember { SnackbarHostState() }
    ),
) {
    when (val screenState = state.innerScreenState) {
        BookshelfEditLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is StorageEditScreenState -> {
            StorageEditRoute(
                state = screenState,
                onBackClick = onBackClick,
                onComplete = onComplete
            )
        }

        is SmbEditScreenState -> {
            SmbEditRoute(state = screenState, onBackClick = onBackClick, onComplete = onComplete)
        }
    }
}
