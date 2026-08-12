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
import com.sorrowblue.comicviewer.file.wrapper.FileInfoWrapper
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
    FileInfoWrapper(
        fileKey = fileKey,
        isOpenFolderEnabled = isOpenFolderEnabled,
    ) { file ->
        val viewModel =
            assistedMetroViewModel<FileInfoViewModel, FileInfoViewModel.Factory> { create(file) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val lazyPagingItems = viewModel.pagingFlow?.collectAsLazyPagingItems()
        FileInfoScreen(
            uiState = uiState,
            lazyPagingItems = lazyPagingItems,
            onBackClick = onBackClick,
            onReadLaterClick = viewModel::updateReadLater,
            onCollectionClick = { onCollectionClick(uiState.file) },
            onOpenFolderClick = {
                onOpenFolderClick(uiState.file)
            },
            modifier = modifier.testTag("FileInfoScreenRoot"),
        )
    }
}
