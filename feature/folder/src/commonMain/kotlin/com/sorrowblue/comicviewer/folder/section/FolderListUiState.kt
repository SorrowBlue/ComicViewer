package com.sorrowblue.comicviewer.folder.section

import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState

internal data class FolderListUiState(
    val title: String = "",
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
    val emphasisPath: String = "",
)
