package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.file.FileInfoSheetAction
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.file.ReadLaterUiState
import com.sorrowblue.comicviewer.file.component.ActionButton
import com.sorrowblue.comicviewer.file.component.ReadlaterButton
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_info_label_add_collection
import comicviewer.feature.file.generated.resources.file_info_label_open_folder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun SheetActionButtons(
    uiState: FileInfoUiState,
    onAction: (FileInfoSheetAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        ReadlaterButton(
            checked = uiState.readLaterUiState.checked,
            loading = uiState.readLaterUiState.loading,
            onClick = { onAction(FileInfoSheetAction.ReadLater) },
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.size(ComicTheme.dimension.padding * 2))

        ActionButton(
            modifier = Modifier.weight(1f),
            onClick = { onAction(FileInfoSheetAction.Collection) },
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
                onClick = { onAction(FileInfoSheetAction.OpenFolder) },
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

@Preview
@Composable
private fun SheetActionButtonsPreview() {
    PreviewTheme {
        Box(modifier = Modifier.background(ComicTheme.colorScheme.background)) {
            SheetActionButtons(
                uiState = FileInfoUiState(
                    readLaterUiState = ReadLaterUiState(
                        checked = false,
                        loading = true
                    ),
                    isOpenFolderEnabled = true,
                    file = fakeBookFile()
                ),
                onAction = {}
            )
        }
    }
}
