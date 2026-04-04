package com.sorrowblue.comicviewer.feature.collection

import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBarUiState
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState

internal data class CollectionScreenUiState(
    val appBarUiState: CollectionAppBarUiState = CollectionAppBarUiState(),
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
)
