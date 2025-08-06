package com.sorrowblue.comicviewer.feature.collection.section

import androidx.compose.material3.AppBarRow2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.file.component.fileListDisplayItem
import com.sorrowblue.comicviewer.file.component.gridSizeItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.canonical.CanonicalAppBar
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

internal sealed interface CollectionAppBarAction {
    data object Back : CollectionAppBarAction
    data object Edit : CollectionAppBarAction
    data object Delete : CollectionAppBarAction
    data object FileListDisplay : CollectionAppBarAction
    data object GridSize : CollectionAppBarAction
    data object Settings : CollectionAppBarAction
}

@Composable
internal fun CanonicalScaffoldState<*>.CollectionAppBar(
    uiState: CollectionAppBarUiState,
    onAction: (CollectionAppBarAction) -> Unit,
) {
    CanonicalAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = { BackIconButton(onClick = { onAction(CollectionAppBarAction.Back) }) },
        actions = {
            AppBarRow2 {
                clickableItem(
                    icon = {
                        Icon(ComicIcons.Edit, null)
                    },
                    label = {},
                    onClick = { onAction(CollectionAppBarAction.Edit) }
                )
                fileListDisplayItem(
                    fileListDisplay = uiState.fileListDisplay,
                    onClick = { onAction(CollectionAppBarAction.FileListDisplay) }
                )
                gridSizeItem(
                    fileListDisplay = uiState.fileListDisplay,
                    onClick = { onAction(CollectionAppBarAction.GridSize) }
                )
                clickableItem(
                    icon = {
                        Icon(ComicIcons.Edit, null)
                    },
                    label = {
                        Text(stringResource(Res.string.collection_label_edit))
                    },
                    onClick = { onAction(CollectionAppBarAction.Edit) }
                )
                clickableItem(
                    icon = {
                        Icon(ComicIcons.Delete, null)
                    },
                    label = {
                        Text(stringResource(Res.string.collection_label_delete))
                    },
                    onClick = { onAction(CollectionAppBarAction.Delete) }
                )
                settingsItem { onAction(CollectionAppBarAction.Settings) }
            }
        },
    )
}
