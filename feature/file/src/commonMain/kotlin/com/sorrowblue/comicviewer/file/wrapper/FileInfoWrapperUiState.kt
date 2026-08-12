/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file.wrapper

import com.sorrowblue.comicviewer.domain.model.file.File

/**
 * Represents the state of preparing file information.
 */
internal sealed interface FileInfoWrapperUiState {

    /**
     * Represents the loading state while preparing file information.
     */
    data object Loading : FileInfoWrapperUiState

    /**
     * Represents the successful state with the prepared file information.
     *
     * @property file The prepared file information.
     * @property isOpenFolderEnabled Indicates whether opening the folder is enabled.
     */
    data class Success(val file: File, val isOpenFolderEnabled: Boolean) : FileInfoWrapperUiState

    /**
     * Represents the error state when preparing file information fails.
     */
    data object Error : FileInfoWrapperUiState
}
