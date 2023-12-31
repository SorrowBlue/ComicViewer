package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs
import com.sorrowblue.comicviewer.framework.ui.ResponsiveDialogScaffold
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

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

@Parcelize
internal data class StorageEditScreenUiState(
    val editType: EditType = EditType.Register,
    val displayName: Input = Input(),
    val dir: Input = Input(),
    val isError: Boolean = false,
    val isProgress: Boolean = false,
) : BookshelfEditScreenUiState, Parcelable

@Composable
internal fun StorageEditRoute(
    state: StorageEditScreenState,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    contentPadding: PaddingValues,
) {
    val uiState = state.uiState
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = state::onResult
    )
    StorageEditScreen(
        uiState = uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onDisplayNameChange = state::onDisplayNameChange,
        onSelectFolderClick = { state.onSelectFolderClick(activityResultLauncher) },
        onSaveClick = { state.onSaveClick(onComplete) },
        contentPadding = contentPadding,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StorageEditScreen(
    uiState: StorageEditScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onSelectFolderClick: () -> Unit,
    onSaveClick: () -> Unit,
    contentPadding: PaddingValues,
    scrollState: ScrollState = rememberScrollState(),
) {
    ResponsiveDialogScaffold(
        title = {
            Text(text = stringResource(id = uiState.editType.title))
        },
        onCloseClick = onBackClick,
        confirmButton = {
            TextButton(onClick = onSaveClick) {
                Text(text = "Save")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentPadding = contentPadding,
    ) { innerPadding ->
        StorageEditContent(
            uiState = uiState,
            onDisplayNameChange = onDisplayNameChange,
            onSelectFolderClick = onSelectFolderClick,
            scrollState = scrollState,
            contentPadding = innerPadding.add(PaddingValues(16.dp))
        )
    }
}

@Composable
private fun StorageEditContent(
    uiState: StorageEditScreenUiState,
    scrollState: ScrollState,
    onDisplayNameChange: (String) -> Unit,
    onSelectFolderClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(contentPadding)
            .drawVerticalScrollbar(scrollState)
            .verticalScroll(scrollState)
    ) {
        DisplayNameField(
            input = uiState.displayName,
            onValueChange = onDisplayNameChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(8.dp))

        FolderSelectField(input = uiState.dir, onClick = onSelectFolderClick)

        Spacer(modifier = Modifier.weight(1f))
    }
}
