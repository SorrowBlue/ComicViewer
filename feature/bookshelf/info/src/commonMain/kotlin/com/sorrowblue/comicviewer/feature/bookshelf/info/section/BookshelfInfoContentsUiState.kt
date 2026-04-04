package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Folder

internal data class BookshelfInfoContentsUiState(
    val bookshelf: Bookshelf,
    val folder: Folder,
    val isScanningFile: Boolean = false,
    val isScanningThumbnail: Boolean = false,
)
