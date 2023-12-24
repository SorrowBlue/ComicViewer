package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.SaveButton
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.Input
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
        onSaveClick = { state.onSaveClick(onComplete) }
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
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = uiState.editType.title))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ComicIcons.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(
                    start = ComicTheme.dimension.margin,
                    end = ComicTheme.dimension.margin,
                    bottom = ComicTheme.dimension.margin
                )
                .verticalScroll(rememberScrollState())
        ) {
            DisplayNameField(
                input = uiState.displayName,
                onValueChange = onDisplayNameChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(8.dp))

            FolderSelectField(input = uiState.dir, onClick = onSelectFolderClick)

            Spacer(
                modifier = Modifier
                    .size(16.dp)
                    .weight(1f)
            )
            SaveButton(
                enabled = !uiState.isError,
                onClick = onSaveClick,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
