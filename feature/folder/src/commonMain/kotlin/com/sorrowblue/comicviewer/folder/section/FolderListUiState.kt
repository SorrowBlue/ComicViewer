/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.folder.section

import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState

internal data class FolderListUiState(
    val title: String = "",
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
    val emphasisPath: String = "",
)
