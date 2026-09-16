/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.section

import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay

internal data class CollectionAppBarUiState(
    val title: String = "",
    val fileListDisplay: FileListDisplay = FileListDisplay.Grid,
)
