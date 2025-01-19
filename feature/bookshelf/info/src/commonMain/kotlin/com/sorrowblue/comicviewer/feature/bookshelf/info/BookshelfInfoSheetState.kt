package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.navigation.NavResult
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalCoroutineScope
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.viewmodel.koinViewModel

internal sealed interface BookshelfInfoSheetStateEvent {
    data object Back : BookshelfInfoSheetStateEvent
    data class Edit(val id: BookshelfId) : BookshelfInfoSheetStateEvent
    data class Remove(val bookshelfId: BookshelfId) : BookshelfInfoSheetStateEvent
}

internal interface BookshelfInfoSheetState {
    val uiState: BookshelfInfoSheetUiState
    val events: EventFlow<BookshelfInfoSheetStateEvent>

    fun onAction(action: BookshelfInfoSheetAction)
    fun onRemoveResult(result: NavResult<Boolean>)
}

@Composable
internal expect fun rememberBookshelfInfoSheetState(
    bookshelfId: BookshelfId,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope = LocalCoroutineScope.current,
    viewModel: BookshelfInfoSheetViewModel = koinViewModel(),
): BookshelfInfoSheetState
