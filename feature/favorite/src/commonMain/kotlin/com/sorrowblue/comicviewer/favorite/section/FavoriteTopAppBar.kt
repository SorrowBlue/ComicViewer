package com.sorrowblue.comicviewer.favorite.section

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
import com.sorrowblue.comicviewer.framework.ui.material3.rememberOverflowMenuState
import comicviewer.feature.favorite.generated.resources.Res
import comicviewer.feature.favorite.generated.resources.favorite_action_delete
import comicviewer.feature.favorite.generated.resources.favorite_title_edit
import org.jetbrains.compose.resources.stringResource

internal data class FavoriteAppBarUiState(
    val title: String = "",
    val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    val gridColumnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
)

internal sealed interface FavoriteTopAppBarAction {
    data object Back : FavoriteTopAppBarAction
    data object Edit : FavoriteTopAppBarAction
    data object Delete : FavoriteTopAppBarAction
    data object FileListDisplay : FavoriteTopAppBarAction
    data object GridSize : FavoriteTopAppBarAction
    data object Settings : FavoriteTopAppBarAction
}

@Composable
internal fun FavoriteTopAppBar(
    uiState: FavoriteAppBarUiState,
    onAction: (FavoriteTopAppBarAction) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    scrollableState: ScrollableState,
) {
    CanonicalTopAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = { BackIconButton(onClick = { onAction(FavoriteTopAppBarAction.Back) }) },
        actions = {
            IconButton(onClick = { onAction(FavoriteTopAppBarAction.Edit) }) {
                Icon(ComicIcons.Edit, stringResource(Res.string.favorite_title_edit))
            }

            OverflowMenu(state = rememberOverflowMenuState()) {
                FileListDisplayItem(
                    fileListDisplay = uiState.fileListDisplay,
                    onClick = { onAction(FavoriteTopAppBarAction.FileListDisplay) }
                )
                GridSizeItem(
                    fileListDisplay = uiState.fileListDisplay,
                    onClick = { onAction(FavoriteTopAppBarAction.GridSize) }
                )
                OverflowMenuItem(
                    text = stringResource(Res.string.favorite_action_delete),
                    icon = ComicIcons.Delete,
                    onClick = { onAction(FavoriteTopAppBarAction.Delete) }
                )
                OverflowMenuItem(
                    text = "stringResource(com.sorrowblue.comicviewer.feature.folder.R.string.folder_action_settings)",
                    icon = ComicIcons.Settings,
                    onClick = { onAction(FavoriteTopAppBarAction.Settings) }
                )
            }
        },
        scrollBehavior = scrollBehavior,
        scrollableState = scrollableState
    )
}
