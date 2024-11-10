package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.os.Parcelable
import android.view.autofill.AutofillManager
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.content.getSystemService
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.isCompactWindowClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.serialization.Serializable

data class BookshelfEditArgs(val editMode: BookshelfEditMode)

@Serializable
sealed class BookshelfEditMode {

    internal abstract val title: Int

    @Serializable
    data class Register(val bookshelfType: BookshelfType) : BookshelfEditMode() {
        @IgnoredOnParcel
        override val title = R.string.bookshelf_edit_title_register
    }

    @Serializable
    data class Edit(val bookshelfId: BookshelfId) : BookshelfEditMode() {
        @IgnoredOnParcel
        override val title = R.string.bookshelf_edit_title_edit
    }
}

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

sealed interface BookshelfEditForm : Parcelable {
    fun <T : BookshelfEditForm> update(displayName: String): T

    val displayName: String
}

@Destination<ExternalModuleGraph>(navArgs = BookshelfEditArgs::class)
@Composable
internal fun BookshelfEditScreen(
    navArgs: BookshelfEditArgs,
    navigator: BookshelfEditScreenNavigator,
) {
    val state = rememberBookshelfEditScreenState(navArgs)
    val isCompact = isCompactWindowClass()
    BackHandler {
        navigator.onBack(state.uiState.editMode)
    }
    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            context.getSystemService<AutofillManager>()!!.commit()
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

    LaunchedEventEffect(event = state.event) {
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
