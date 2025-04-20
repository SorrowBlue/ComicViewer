package com.sorrowblue.comicviewer.feature.collection.section

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.file.component.FileListDisplayItem
import com.sorrowblue.comicviewer.file.component.GridSizeItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalTopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsOverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.rememberOverflowMenuState
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_delete
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
internal fun CollectionAppBar(
    uiState: CollectionAppBarUiState,
    onAction: (CollectionAppBarAction) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    scrollableState: ScrollableState,
) {
    CanonicalTopAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = { BackIconButton(onClick = { onAction(CollectionAppBarAction.Back) }) },
        actions = {
            IconButton(onClick = { onAction(CollectionAppBarAction.Edit) }) {
                Icon(ComicIcons.Edit, null)
            }

            OverflowMenu(state = rememberOverflowMenuState()) {
                FileListDisplayItem(
                    fileListDisplay = uiState.fileListDisplay,
                    onClick = { onAction(CollectionAppBarAction.FileListDisplay) }
                )
                GridSizeItem(
                    fileListDisplay = uiState.fileListDisplay,
                    onClick = { onAction(CollectionAppBarAction.GridSize) }
                )
                OverflowMenuItem(
                    text = stringResource(Res.string.collection_label_delete),
                    icon = ComicIcons.Delete,
                    onClick = { onAction(CollectionAppBarAction.Delete) }
                )
                SettingsOverflowMenuItem(onClick = { onAction(CollectionAppBarAction.Settings) })
            }
        },
        scrollBehavior = scrollBehavior,
        scrollableState = scrollableState
    )
}
