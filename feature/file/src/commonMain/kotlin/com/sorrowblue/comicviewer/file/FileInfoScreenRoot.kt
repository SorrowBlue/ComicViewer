package com.sorrowblue.comicviewer.file

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.file.File

@Composable
context(context: FileInfoScreenContext)
fun FileInfoScreenRoot(
    fileKey: File.Key,
    isOpenFolderEnabled: Boolean,
    onBackClick: () -> Unit,
    onCollectionClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    val prepareState = rememberFileInfoPrepareState(fileKey, isOpenFolderEnabled)
    when (val uiState = prepareState.uiState) {
        is FileInfoPrepareUiState.Success -> {
            val state = rememberFileInfoScreenState(uiState.file, uiState.isOpenFolderEnabled)
            FileInfoScreen(
                uiState = state.uiState,
                lazyPagingItems = state.lazyPagingItems,
                onBackClick = onBackClick,
                onReadLaterClick = state::onReadLaterClick,
                onCollectionClick = { onCollectionClick(state.uiState.file) },
                onOpenFolderClick = {
                    onOpenFolderClick(state.uiState.file)
                },
                modifier = modifier.testTag("FileInfoScreenRoot")
            )
        }

        FileInfoPrepareUiState.Loading -> {
            LoadingContents()
        }

        FileInfoPrepareUiState.Error -> {
            ErrorContents()
        }
    }
}
