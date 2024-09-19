package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.rememberFolderSelectFieldState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.EditDialog
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.EditScreen
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import soil.form.FormPolicy
import soil.form.compose.Controller
import soil.form.compose.Form
import soil.form.compose.rememberSubmissionRuleAutoControl

internal data class InternalStorageEditScreenUiState(
    val form: InternalStorageEditScreenForm,
    override val editMode: BookshelfEditMode,
) : BookshelfEditScreenUiState

@Parcelize
internal data class InternalStorageEditScreenForm(
    override val displayName: String = "",
    val path: Uri? = null,
) : BookshelfEditForm {

    override fun <T : BookshelfEditForm> update(displayName: String): T {
        @Suppress("UNCHECKED_CAST")
        return copy(displayName = displayName) as T
    }
}

@Composable
internal fun InternalStorageEditDialogScreen(
    isDialog: Boolean,
    uiState: InternalStorageEditScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSubmit: suspend (InternalStorageEditScreenForm) -> Unit,
    scrollState: ScrollState = rememberScrollState(),
) {
    Form(
        key = "InternalStorageEditDialogScreen",
        onSubmit = onSubmit,
        initialValue = uiState.form,
        policy = FormPolicy.Default
    ) {
        Controller(rememberSubmissionRuleAutoControl()) { submission ->
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val openDocumentTreeCancelMessage =
                stringResource(id = R.string.bookshelf_edit_msg_cancelled_folder_selection)
            val folderSelectFieldState = rememberFolderSelectFieldState(onOpenDocumentTreeCancel = {
                if (isDialog) {
                    Toast.makeText(
                        context,
                        openDocumentTreeCancelMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(openDocumentTreeCancelMessage)
                    }
                }
            })
            val content = remember {
                movableContentWithReceiverOf<ColumnScope> {
                    val dimension = ComicTheme.dimension
                    DisplayNameField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = !submission.isSubmitting
                    )
                    FolderSelectField(
                        state = folderSelectFieldState,
                        enabled = !submission.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.padding)
                    )
                }
            }
            if (isDialog) {
                EditDialog(
                    uiState = uiState,
                    onDismissRequest = onBackClick,
                    submission = submission,
                    scrollState = scrollState,
                    content = content,
                )
            } else {
                EditScreen(
                    uiState = uiState,
                    onBackClick = onBackClick,
                    submission = submission,
                    scrollState = scrollState,
                    snackbarHostState = snackbarHostState,
                    content = content,
                )
            }
        }
    }
}

@PreviewMultiScreen
@Composable
private fun InternalStorageEditScreenPreview() {
    PreviewTheme {
        val isCompact = isCompactWindowClass()
        InternalStorageEditDialogScreen(
            isDialog = !isCompact,
            uiState = InternalStorageEditScreenUiState(
                form = InternalStorageEditScreenForm(),
                editMode = BookshelfEditMode.Register(BookshelfType.DEVICE)
            ),
            snackbarHostState = SnackbarHostState(),
            onBackClick = {},
            onSubmit = {}
        )
    }
}
