package com.sorrowblue.comicviewer.folder.section

import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType

internal data class FolderAppBarUiState(
    val title: String = "",
    val folderScopeOnly: Boolean = false,
    val sortType: SortType = SortType.Name(true),
    val showSearch: Boolean = false,
)
