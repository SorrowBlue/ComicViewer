/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.file.section.FileInfoButtonsUiState

internal data class FileInfoScreenUiState(
    val file: File,
    val attribute: FileAttribute? = null,
    val fileInfoButtonsUiState: FileInfoButtonsUiState = FileInfoButtonsUiState(),
)

@Composable
internal fun rememberFileInfoScreenState(
    lazyPagingItems: LazyPagingItems<BookThumbnail>?,
    uiState: FileInfoUiState,
    onReadLaterClick: () -> Unit,
    file: File,
): FileInfoScreenState = remember(lazyPagingItems, uiState) {
    FileInfoScreenStateImpl(
        lazyPagingItems = lazyPagingItems,
        uiStateProvider = {
            when (uiState) {
                is FileInfoUiState.Success -> FileInfoScreenUiState(
                    file = uiState.file,
                    attribute = uiState.attribute,
                    fileInfoButtonsUiState = uiState.fileInfoButtonsUiState,
                )

                else -> FileInfoScreenUiState(
                    file = file,
                )
            }
        },
        onReadLaterClickAction = onReadLaterClick,
    )
}

internal interface FileInfoScreenState {
    val uiState: FileInfoScreenUiState
    val lazyPagingItems: LazyPagingItems<BookThumbnail>?

    fun onReadLaterClick()
}

private class FileInfoScreenStateImpl(
    override val lazyPagingItems: LazyPagingItems<BookThumbnail>?,
    private val uiStateProvider: () -> FileInfoScreenUiState,
    private val onReadLaterClickAction: () -> Unit,
) : FileInfoScreenState {

    override val uiState: FileInfoScreenUiState get() = uiStateProvider()

    override fun onReadLaterClick() {
        onReadLaterClickAction()
    }
}
