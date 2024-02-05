package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectField
import com.sorrowblue.comicviewer.framework.ui.ResponsiveDialogScaffold
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class StorageEditScreenUiState(
    val editType: EditType = EditType.Register,
    val displayName: Input = Input(),
    val dir: Input = Input(),
    val isError: Boolean = false,
    val isProgress: Boolean = false,
) : BookshelfEditScreenUiState

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
    scrollState: ScrollState = rememberScrollState(),
) {
    ResponsiveDialogScaffold(
        title = {
            Text(text = stringResource(id = uiState.editType.title))
        },
        onCloseClick = onBackClick,
        confirmButton = {
            TextButton(onClick = onSaveClick) {
                Text(text = stringResource(id = R.string.bookshelf_edit_label_save))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
