/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file.section

internal data class FileInfoButtonsUiState(
    val readLaterChecked: Boolean = false,
    val readLaterLoading: Boolean = false,
    val isOpenFolderEnabled: Boolean = false,
)
