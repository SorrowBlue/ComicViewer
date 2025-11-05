package com.sorrowblue.comicviewer.feature.collection.section

import androidx.compose.material3.AppBarRow2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.file.component.fileListDisplayItem
import com.sorrowblue.comicviewer.file.component.gridSizeItem
import com.sorrowblue.comicviewer.file.component.rememberFileListDisplayItemState
import com.sorrowblue.comicviewer.file.component.rememberGridSizeItemState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.settingsItem
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_delete
import comicviewer.feature.collection.generated.resources.collection_label_edit
import org.jetbrains.compose.resources.stringResource

internal data class CollectionAppBarUiState(
    val title: String = "",
    val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    val gridColumnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
)

@Composable
internal fun CollectionAppBar(
    uiState: CollectionAppBarUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = { BackIconButton(onClick = onBackClick) },
        actions = {
            val gridSizeItemState = rememberGridSizeItemState()
            val fileListDisplayItemState = rememberFileListDisplayItemState()
            AppBarRow2 {
                clickableItem(
                    icon = {
                        Icon(ComicIcons.Edit, null)
                    },
                    label = {
                        Text(stringResource(Res.string.collection_label_edit))
                    },
                    onClick = onEditClick
                )
                fileListDisplayItemState.fileListDisplayItem()
                gridSizeItemState.gridSizeItem()
                clickableItem(
                    icon = {
                        Icon(ComicIcons.Delete, null)
                    },
                    label = {
                        Text(stringResource(Res.string.collection_label_delete))
                    },
                    onClick = onDeleteClick
                )
                settingsItem(onClick = onSettingsClick)
            }
        },
    )
}
