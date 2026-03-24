package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.file.component.ActionButton
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_info_label_add_collection
import comicviewer.feature.file.generated.resources.file_info_label_add_read_later
import comicviewer.feature.file.generated.resources.file_info_label_open_folder
import comicviewer.feature.file.generated.resources.file_info_label_remove_read_later
import org.jetbrains.compose.resources.stringResource

internal data class FileInfoButtonsUiState(
    val readLaterChecked: Boolean = false,
    val readLaterLoading: Boolean = false,
    val isOpenFolderEnabled: Boolean = false,
)

@Composable
internal fun FileInfoButtons(
    uiState: FileInfoButtonsUiState,
    onReadLaterClick: () -> Unit,
    onCollectionClick: () -> Unit,
    onOpenFolderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        ActionButton(
            modifier = Modifier.testTag("ReadLaterButton").fillMaxWidth(),
            onClick = onReadLaterClick,
            icon = ComicIcons.WatchLater,
            text = {
                if (uiState.readLaterChecked) {
                    Text(text = stringResource(Res.string.file_info_label_remove_read_later))
                } else {
                    Text(text = stringResource(Res.string.file_info_label_add_read_later))
                }
            },
            enabled = !uiState.readLaterLoading,
            loading = uiState.readLaterLoading,
        )
        ActionButton(
            modifier = Modifier.testTag("AddCollectionButton").fillMaxWidth(),
            onClick = onCollectionClick,
            icon = ComicIcons.Favorite,
            text = {
                Text(text = stringResource(Res.string.file_info_label_add_collection))
            },
        )
        if (uiState.isOpenFolderEnabled) {
            ActionButton(
                modifier = Modifier.testTag("OpenFolderButton").fillMaxWidth(),
                onClick = onOpenFolderClick,
                icon = ComicIcons.FolderOpen,
                text = {
                    Text(text = stringResource(Res.string.file_info_label_open_folder))
                },
            )
        }
        ActionButton(
            modifier = Modifier.testTag("RefreshButton").fillMaxWidth(),
            onClick = {},
            icon = ComicIcons.Refresh,
            text = {
                Text(text = "Regenerate Thumbnail")
            },
            enabled = false,
        )
    }
}
