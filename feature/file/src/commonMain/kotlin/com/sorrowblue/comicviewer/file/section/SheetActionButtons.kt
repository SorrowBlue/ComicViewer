package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.file.FileInfoSheetAction
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.file.component.ReadlaterButton
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_info_label_add_collection
import comicviewer.feature.file.generated.resources.file_info_label_open_folder
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SheetActionButtons(
    uiState: FileInfoUiState,
    onAction: (FileInfoSheetAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ReadlaterButton(
            uiState = uiState.readLaterUiState,
            onClick = { onAction(FileInfoSheetAction.ReadLater) }
        )
        OutlinedButton(
            onClick = { onAction(FileInfoSheetAction.Collection) },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ComicTheme.dimension.minPadding * 4)
        ) {
            Icon(imageVector = ComicIcons.Favorite, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.file_info_label_add_collection))
        }
        if (uiState.isOpenFolderEnabled) {
            OutlinedButton(
                onClick = { onAction(FileInfoSheetAction.OpenFolder) },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ComicTheme.dimension.minPadding * 4)
            ) {
                Icon(imageVector = ComicIcons.FolderOpen, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(Res.string.file_info_label_open_folder))
            }
        }
    }
}
