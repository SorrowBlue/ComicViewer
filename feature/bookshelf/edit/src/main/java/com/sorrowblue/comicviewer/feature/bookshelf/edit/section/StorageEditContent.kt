package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.SaveButton
import com.sorrowblue.comicviewer.framework.ui.material3.Input

data class StorageEditContentUiState(
    val displayName: Input = Input(),
    val dir: Input = Input(),
    val isError: Boolean = false,
    val isProgress: Boolean = false,
)

@Composable
internal fun MobileStorageEditContent(
    contentState: DeviceStorageEditScreenState2,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = contentState.state
    MobileStorageEditContent(
        uiState = state.uiState,
        onDisplayNameChange = state::onDisplayNameChange,
        onSelectFolderClick = contentState::onOpenFolderClick,
        onSaveClick = { state.onSaveClick(onComplete) },
        modifier = modifier
    )
}

@Composable
private fun MobileStorageEditContent(
    uiState: StorageEditContentUiState,
    onDisplayNameChange: (String) -> Unit,
    onSelectFolderClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
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

@Composable
internal fun TabletStorageEditContent(
    contentState: DeviceStorageEditScreenState2,
    modifier: Modifier = Modifier,
) {
    val state = contentState.state
    TabletStorageEditContent(
        uiState = state.uiState,
        onDisplayNameChange = state::onDisplayNameChange,
        onSelectFolderClick = contentState::onOpenFolderClick,
        modifier = modifier
    )
}

@Composable
private fun TabletStorageEditContent(
    uiState: StorageEditContentUiState,
    onDisplayNameChange: (String) -> Unit,
    onSelectFolderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        DisplayNameField(
            input = uiState.displayName,
            onValueChange = onDisplayNameChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(8.dp))

        FolderSelectField(input = uiState.dir, onClick = onSelectFolderClick)
    }
}
