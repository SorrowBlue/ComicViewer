package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.annotation.CallSuper
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditViewModel
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface DeviceStorageEditScreenState2 : BookshelfEditContentState {
    val state: StorageEditContentState
    val activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

    val openDocumentTreeIntent
        get() = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        }

    fun onOpenFolderClick() {
        activityResultLauncher.launch(openDocumentTreeIntent)
    }
}

@Stable
class DeviceStorageEditScreenState2Impl(
    override val state: StorageEditContentState,
    override val activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
) : DeviceStorageEditScreenState2

interface StorageEditContentState {

    fun init(internalStorage: InternalStorage, folder: Folder): StorageEditContentState

    var uiState: StorageEditContentUiState

    val snackbarHostState: SnackbarHostState

    fun onDisplayNameChange(text: String) {
        uiState = uiState.copy(displayName = Input(value = text, isError = text.isBlank()))
    }

    @CallSuper
    fun onSaveClick(complete: () -> Unit) {
        onDisplayNameChange(uiState.displayName.value)
        uiState = uiState.copy(dir = uiState.dir.copy(isError = uiState.dir.value.isBlank()))
        val isError = uiState.displayName.isError || uiState.dir.isError
        uiState = uiState.copy(isError = isError)
    }

    fun onResult(it: ActivityResult)
}

@Composable
internal fun rememberStorageEditContentState(
    args: BookshelfEditArgs,
    snackbarHostState: SnackbarHostState,
    viewModel: BookshelfEditViewModel,
    scope: CoroutineScope,
    context: Context,
): StorageEditContentState = remember {
    StorageEditContentStateImpl(
        args,
        snackbarHostState,
        viewModel,
        scope,
        context
    )
}

@Stable
private class StorageEditContentStateImpl(
    val args: BookshelfEditArgs,
    override val snackbarHostState: SnackbarHostState,
    private val viewModel: BookshelfEditViewModel,
    private val scope: CoroutineScope,
    private val context: Context,
) : StorageEditContentState {

    private var folderUri: Uri? = null
    override fun init(internalStorage: InternalStorage, folder: Folder): StorageEditContentState {
        folderUri = folder.path.toUri()
        uiState = StorageEditContentUiState(
            displayName = Input(value = internalStorage.displayName),
            dir = Input(value = folderUri?.lastPathSegment?.split(":")?.lastOrNull().orEmpty())
        )
        return this
    }

    override var uiState by mutableStateOf(StorageEditContentUiState())

    override fun onSaveClick(complete: () -> Unit) {
        super.onSaveClick(complete)
        if (uiState.isError) {
            scope.launch {
                snackbarHostState.showSnackbar("Please check your entries.")
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

    override fun onResult(it: ActivityResult) {
        it.data?.data?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            folderUri = it
            val dir = it.lastPathSegment?.split(":")?.lastOrNull().orEmpty()
            uiState = uiState.copy(dir = Input(value = dir))
        } ?: run {
            uiState = uiState.copy(dir = uiState.dir.copy(isError = true))
            scope.launch {
                snackbarHostState.showSnackbar("フォルダの選択がキャンセルされました。")
            }
        }
    }
}
