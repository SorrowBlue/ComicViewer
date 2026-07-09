/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.search.section

import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchListUiState(
    val query: String = "",
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
)
