package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType

interface BookshelfEditScreenNavigator {
    fun navigateUp()
    fun onComplete()
}

data class BookshelfEditArgs(
    val bookshelfId: BookshelfId = BookshelfId(),
    val bookshelfType: BookshelfType = BookshelfType.DEVICE,
)

@Destination<ExternalModuleGraph>(navArgs = BookshelfEditArgs::class)
@Composable
internal fun BookshelfEditScreen(
    args: BookshelfEditArgs,
    navigator: BookshelfEditScreenNavigator,
) {
    BookshelfEditScreen(
        args = args,
        onBackClick = navigator::navigateUp,
        onComplete = navigator::onComplete
    )
}

@Composable
private fun BookshelfEditScreen(
    args: BookshelfEditArgs,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
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
                onComplete = onComplete
            )
        }

        is SmbEditScreenState -> {
            SmbEditScreen(
                state = screenState,
                onBackClick = onBackClick,
                onComplete = onComplete
            )
        }
    }
}
