package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.file.component.ActionButton
import com.sorrowblue.comicviewer.file.component.ReadlaterButton
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_info_label_add_collection
import comicviewer.feature.file.generated.resources.file_info_label_open_folder
import org.jetbrains.compose.resources.stringResource

internal data class SheetActionButtonsUiState(
    val readLaterChecked: Boolean = false,
    val readLaterLoading: Boolean = false,
    val isOpenFolderEnabled: Boolean = false,
)

@Composable
internal fun SheetActionButtons(
    uiState: SheetActionButtonsUiState,
    onReadLaterClick: () -> Unit,
    onCollectionClick: () -> Unit,
    onOpenFolderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        ReadlaterButton(
            checked = uiState.readLaterChecked,
            loading = uiState.readLaterLoading,
            onClick = onReadLaterClick,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.size(ComicTheme.dimension.padding * 2))

        ActionButton(
            modifier = Modifier.weight(1f),
            onClick = onCollectionClick,
            icon = {
                Icon(ComicIcons.Favorite, null)
            },
            text = {
                Text(text = stringResource(Res.string.file_info_label_add_collection))
            },
        )
        if (uiState.isOpenFolderEnabled) {
            Spacer(Modifier.size(ComicTheme.dimension.padding * 2))
            ActionButton(
                modifier = Modifier.weight(1f),
                onClick = onOpenFolderClick,
                icon = {
                    Icon(ComicIcons.FolderOpen, null)
                },
                text = {
                    Text(text = stringResource(Res.string.file_info_label_open_folder))
                },
            )
        }
    }
}
