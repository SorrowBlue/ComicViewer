package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.rememberFolderSelectFieldState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.EditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.EditorDialog
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.NotificationManager
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_msg_cancelled_folder_selection
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import soil.form.compose.rememberForm

internal data class InternalStorageEditScreenUiState(
    val form: InternalStorageEditScreenForm,
    override val editMode: BookshelfEditMode,
) : BookshelfEditScreenUiState

@Serializable
internal data class InternalStorageEditScreenForm(
    override val displayName: String = "",
    val path: String? = null,
    override val isRunning: Boolean = false,
) : BookshelfEditForm {

    override fun <T : BookshelfEditForm> update(displayName: String, isRunning: Boolean?): T {
        @Suppress("UNCHECKED_CAST")
        return copy(displayName = displayName, isRunning = isRunning ?: this.isRunning) as T
    }
}

@Composable
internal fun InternalStorageEditDialogScreen(
    isDialog: Boolean,
    uiState: InternalStorageEditScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSubmit: (InternalStorageEditScreenForm) -> Unit,
    scrollState: ScrollState = rememberScrollState(),
    notificationManager: NotificationManager = koinInject(),
) {
    val form = rememberForm(
        initialValue = uiState.form,
        onSubmit = onSubmit,
        saver = kSerializableSaver<InternalStorageEditScreenForm>()
    )
    val scope = rememberCoroutineScope()
    val openDocumentTreeCancelMessage =
        stringResource(Res.string.bookshelf_edit_msg_cancelled_folder_selection)
    val folderSelectFieldState = rememberFolderSelectFieldState(
        form = form,
        onOpenDocumentTreeCancel = {
            if (isDialog) {
                notificationManager.toast(
                    openDocumentTreeCancelMessage,
                    NotificationManager.LENGTH_SHORT
                )
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(openDocumentTreeCancelMessage)
                }
            }
        }
    )
    val content = remember {
        movableContentWithReceiverOf<ColumnScope> {
            val dimension = ComicTheme.dimension
            DisplayNameField(
                form = form,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            FolderSelectField(
                state = folderSelectFieldState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimension.padding)
            )
        }
    }
    if (isDialog) {
        EditorDialog(
            form = form,
            uiState = uiState,
            onDismissRequest = onBackClick,
            scrollState = scrollState,
            content = content,
        )
    } else {
        EditScreen(
            form = form,
            uiState = uiState,
            onBackClick = onBackClick,
            scrollState = scrollState,
            snackbarHostState = snackbarHostState,
            content = content,
        )
    }
}
