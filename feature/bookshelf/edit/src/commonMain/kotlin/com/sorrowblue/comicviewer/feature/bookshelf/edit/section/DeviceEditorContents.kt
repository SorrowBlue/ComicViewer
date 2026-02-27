package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.bookshelf.edit.DeviceEditForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectFieldState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import soil.form.annotation.InternalSoilFormApi
import soil.form.compose.Form

@OptIn(InternalSoilFormApi::class)
@Composable
internal fun DeviceEditorContents(
    form: Form<DeviceEditForm>,
    folderSelectFieldState: FolderSelectFieldState,
    uiState: BookshelfEditScreenUiState,
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
