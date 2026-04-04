package com.sorrowblue.comicviewer.folder

import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.FolderListUiState

internal data class FolderScreenUiState(
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val folderListUiState: FolderListUiState = FolderListUiState(),
)
