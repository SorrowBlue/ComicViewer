package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class StorageEditScreenState(
    uiState: StorageEditScreenUiState,
    private var folderUri: Uri?,
    val snackbarHostState: SnackbarHostState,
    private val args: BookshelfEditArgs,
    private val context: Context,
    private val scope: CoroutineScope,
    private val viewModel: BookshelfEditViewModel,
) : BookshelfEditInnerScreenState<StorageEditScreenUiState>() {

    override var uiState by mutableStateOf(uiState)

    fun onDisplayNameChange(text: String) {
        uiState = uiState.copy(displayName = Input(value = text, isError = text.isBlank()))
    }

    fun onSelectFolderClick(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        launcher.launch(
            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            }
        )
    }

    fun onResult(activityResult: ActivityResult) {
        activityResult.data?.data?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            folderUri = it
            val dir = it.lastPathSegment?.split(":")?.lastOrNull().orEmpty()
            uiState = uiState.copy(dir = Input(dir))
        } ?: run {
            uiState = uiState.copy(dir = uiState.dir.copy(isError = uiState.dir.value.isBlank()))
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.bookshelf_edit_msg_cancelled_folder_selection))
            }
        }
    }

    fun onSaveClick(complete: () -> Unit) {
        onDisplayNameChange(uiState.displayName.value)
        uiState = uiState.copy(dir = uiState.dir.copy(isError = uiState.dir.value.isBlank()))
        val isError = uiState.displayName.isError || uiState.dir.isError
        uiState = uiState.copy(isError = isError)
        if (uiState.isError) {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.bookshelf_edit_msg_input_error))
            }
            return
        }
        uiState = uiState.copy(isProgress = true)
        val internalStorage = InternalStorage(
            id = args.bookshelfId,
            displayName = uiState.displayName.value
        )
        viewModel.save(internalStorage, folderUri.toString()) {
            uiState = uiState.copy(isProgress = false)
            complete()
        }
    }
}
