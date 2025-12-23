package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.bookshelf.edit.InternalStorageEditScreenState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectField
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import soil.form.annotation.InternalSoilFormApi

@OptIn(InternalSoilFormApi::class)
@Composable
internal fun InternalStorageEditorContents(
    state: InternalStorageEditScreenState,
    onBackClick: () -> Unit,
    scrollState: ScrollState = rememberScrollState(),
) {
    val content = remember {
        movableContentWithReceiverOf<ColumnScope> {
            val dimension = ComicTheme.dimension
            DisplayNameField(
                form = state.form,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            FolderSelectField(
                state = state.folderSelectFieldState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimension.padding),
            )
        }
    }
    EditorContents(
        form = state.form,
        uiState = state.uiState,
        onBackClick = onBackClick,
        scrollState = scrollState,
        content = content,
    )
}
