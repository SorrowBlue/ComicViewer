package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.framework.ui.BackHandler
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
internal data class BookshelfEdit(val editMode: BookshelfEditMode)

interface BookshelfEditScreenNavigator {
    fun onBack(editMode: BookshelfEditMode)
    fun onComplete()
}

sealed interface BookshelfEditScreenUiState {

    val editMode: BookshelfEditMode

    data class Loading(
        override val editMode: BookshelfEditMode,
    ) : BookshelfEditScreenUiState
}

@Destination<BookshelfEdit>(style = DestinationStyle.Auto::class)
@Composable
internal fun BookshelfEditScreen(
    route: BookshelfEdit,
    navigator: BookshelfEditScreenNavigator = koinInject(),
    androidAutofillManager: AndroidAutofillManager = koinInject(),
    state: BookshelfEditScreenState = rememberBookshelfEditScreenState(route.editMode),
    isCompact: Boolean = isCompactWindowClass(),
) {
    BackHandler {
        navigator.onBack(state.uiState.editMode)
    }
    DisposableEffect(Unit) {
        onDispose {
            androidAutofillManager.commit()
        }
    }
    when (val uiState = state.uiState) {
        is BookshelfEditScreenUiState.Loading ->
            BookshelfEditLoadingScreen(
                isDialog = !isCompact,
                uiState = uiState,
                onBackClick = { navigator.onBack(uiState.editMode) }
            )

        is InternalStorageEditScreenUiState ->
            InternalStorageEditDialogScreen(
                isDialog = !isCompact,
                uiState = uiState,
                snackbarHostState = state.snackbarHostState,
                onBackClick = { navigator.onBack(uiState.editMode) },
                onSubmit = { state.onSubmit(it) }
            )

        is SmbEditScreenUiState ->
            SmbEditScreen(
                isDialog = !isCompact,
                uiState = uiState,
                snackbarHostState = state.snackbarHostState,
                onBackClick = { navigator.onBack(uiState.editMode) },
                onSubmit = { state.onSubmit(it) }
            )
    }

    EventEffect(state.events) {
        when (it) {
            BookshelfEditScreenEvent.Complete -> navigator.onComplete()
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    DisposableEffect(Unit) {
        onDispose {
            keyboardController?.hide()
        }
    }
}

internal expect class AndroidAutofillManager {
    fun commit()
}
