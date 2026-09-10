/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.file

import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.framework.ui.file.section.FileInfoButtonsUiState

internal data class FileInfoScreenUiState(
    val file: File,
    val attribute: FileAttribute? = null,
    val fileInfoButtonsUiState: FileInfoButtonsUiState = FileInfoButtonsUiState(),
)
