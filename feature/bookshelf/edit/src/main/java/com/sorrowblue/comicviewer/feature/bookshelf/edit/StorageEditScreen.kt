package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectField
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.component.CloseIconButton
import com.sorrowblue.comicviewer.framework.ui.marginPadding
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

@OptIn(ExperimentalMaterial3Api::class)
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
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = uiState.editType.title)) },
                navigationIcon = { CloseIconButton(onClick = onBackClick) },
                actions = {
                    TextButton(onClick = onSaveClick) {
                        Text(text = stringResource(id = R.string.bookshelf_edit_label_save))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        StorageEditContent(
            uiState = uiState,
            onDisplayNameChange = onDisplayNameChange,
            onSelectFolderClick = onSelectFolderClick,
            scrollState = scrollState,
            contentPadding = contentPadding
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
    val dimension = ComicTheme.dimension
    Column(
        modifier
            .fillMaxSize()
            .imePadding()
            .drawVerticalScrollbar(scrollState)
            .verticalScroll(scrollState)
            .windowInsetsPadding(contentPadding.asWindowInsets())
            .marginPadding(dimension = ComicTheme.dimension, horizontal = true, bottom = true)
    ) {
        DisplayNameField(
            input = uiState.displayName,
            onValueChange = onDisplayNameChange,
            modifier = Modifier.fillMaxWidth()
        )
        FolderSelectField(
            input = uiState.dir,
            onClick = onSelectFolderClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimension.targetSpacing)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@PreviewMultiScreen
private fun PreviewStorageEditScreen() {
    PreviewTheme {
        StorageEditScreen(
            uiState = StorageEditScreenUiState(),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = { },
            onDisplayNameChange = { },
            onSelectFolderClick = { },
            onSaveClick = { }
        )
    }
}
