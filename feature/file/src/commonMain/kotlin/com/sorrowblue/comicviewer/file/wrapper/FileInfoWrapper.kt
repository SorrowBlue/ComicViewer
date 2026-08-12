/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file.wrapper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.file.ErrorContents
import com.sorrowblue.comicviewer.file.LoadingContents
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
internal fun FileInfoWrapper(
    fileKey: File.Key,
    isOpenFolderEnabled: Boolean,
    content: @Composable (File) -> Unit,
) {
    val viewModel =
        assistedMetroViewModel<FileInfoWrapperViewModel, FileInfoWrapperViewModel.Factory> {
            create(fileKey = fileKey, isOpenFolderEnabled = isOpenFolderEnabled)
        }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val uiState = uiState) {
        FileInfoWrapperUiState.Loading -> LoadingContents()
        is FileInfoWrapperUiState.Success -> content(uiState.file)
        FileInfoWrapperUiState.Error -> ErrorContents()
    }
}
