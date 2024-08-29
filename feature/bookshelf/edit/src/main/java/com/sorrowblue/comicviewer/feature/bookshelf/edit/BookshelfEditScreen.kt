package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.os.Parcelable
import android.view.autofill.AutofillManager
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.content.getSystemService
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.isCompact
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

data class BookshelfEditArgs(val editMode: BookshelfEditMode)

sealed class BookshelfEditMode : Parcelable {

    internal abstract val title: Int

    @Parcelize
    data class Register(val bookshelfType: BookshelfType) : BookshelfEditMode() {
        @IgnoredOnParcel
        override val title = R.string.bookshelf_edit_title_register
    }

    @Parcelize
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
    val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
    val state = rememberBookshelfEditScreenState(navArgs)
    val isCompact by remember(windowAdaptiveInfo) {
        mutableStateOf(windowAdaptiveInfo.isCompact)
    }
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
                onBackClick = { navigator.onBack(uiState.editMode) })

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
