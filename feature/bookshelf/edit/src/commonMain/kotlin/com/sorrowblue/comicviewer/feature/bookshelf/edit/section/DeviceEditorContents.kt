/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectFieldState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.BookshelfEditorScreenUiState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.DeviceEditorForm
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import soil.form.compose.Form

@Composable
internal fun DeviceEditorContents(
    form: Form<DeviceEditorForm>,
    folderSelectFieldState: FolderSelectFieldState,
    uiState: BookshelfEditorScreenUiState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val dimension = ComicTheme.dimension
        DisplayNameField(
            form = form,
            enabled = !uiState.progress,
            modifier = Modifier
                .fillMaxWidth(),
        )
        FolderSelectField(
            state = folderSelectFieldState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimension.padding),
        )
    }
}
