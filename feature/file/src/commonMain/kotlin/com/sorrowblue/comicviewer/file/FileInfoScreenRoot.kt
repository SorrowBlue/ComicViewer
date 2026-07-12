/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun FileInfoScreenRoot(
    fileKey: File.Key,
    isOpenFolderEnabled: Boolean,
    onBackClick: () -> Unit,
    onCollectionClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = assistedMetroViewModel<FileInfoViewModel, FileInfoViewModel.Factory> {
        create(fileKey, isOpenFolderEnabled)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    when (val current = uiState) {
        FileInfoUiState.Loading -> {
            LoadingContents()
        }

        FileInfoUiState.Error -> {
            ErrorContents()
        }

        is FileInfoUiState.Success -> {
            val state = rememberFileInfoScreenState(
                lazyPagingItems = lazyPagingItems,
                uiState = current,
                onReadLaterClick = viewModel::onReadLaterClick,
                file = current.file,
            )
            FileInfoScreen(
                uiState = state.uiState,
                lazyPagingItems = state.lazyPagingItems,
                onBackClick = onBackClick,
                onReadLaterClick = state::onReadLaterClick,
                onCollectionClick = { onCollectionClick(state.uiState.file) },
                onOpenFolderClick = {
                    onOpenFolderClick(state.uiState.file)
                },
                modifier = modifier.testTag("FileInfoScreenRoot"),
            )
        }
    }
}
