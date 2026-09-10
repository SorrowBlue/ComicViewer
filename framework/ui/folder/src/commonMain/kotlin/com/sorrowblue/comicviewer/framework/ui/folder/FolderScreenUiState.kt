/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.folder

import com.sorrowblue.comicviewer.framework.ui.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.framework.ui.folder.section.FolderListUiState

internal data class FolderScreenUiState(
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val folderListUiState: FolderListUiState = FolderListUiState(),
)
