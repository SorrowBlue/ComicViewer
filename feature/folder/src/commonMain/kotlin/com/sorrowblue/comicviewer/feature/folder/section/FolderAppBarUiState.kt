/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.folder.section

import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType

internal data class FolderAppBarUiState(
    val title: String = "",
    val folderScopeOnly: Boolean = false,
    val includeSubfolders: Boolean = false,
    val sortType: SortType = SortType.Name(true),
    val showSearch: Boolean = false,
    val fileListDisplay: FileListDisplay = FileListDisplay.Grid,
    val showHiddenFiles: Boolean = false,
)
