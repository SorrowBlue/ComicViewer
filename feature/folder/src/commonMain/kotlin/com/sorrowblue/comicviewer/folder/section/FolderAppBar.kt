package com.sorrowblue.comicviewer.folder.section

import androidx.compose.material3.AppBarRow2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.file.component.fileListDisplayItem
import com.sorrowblue.comicviewer.file.component.gridSizeItem
import com.sorrowblue.comicviewer.file.component.hiddenFilesToggleableItem
import com.sorrowblue.comicviewer.file.component.rememberFileListDisplayItemState
import com.sorrowblue.comicviewer.file.component.rememberGridSizeItemState
import com.sorrowblue.comicviewer.file.component.rememberHiddenFilesToggleableItemState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import comicviewer.feature.folder.generated.resources.Res
import comicviewer.feature.folder.generated.resources.folder_action_search
import comicviewer.feature.folder.generated.resources.folder_action_settings
import org.jetbrains.compose.resources.stringResource

internal data class FolderAppBarUiState(
    val title: String = "",
    val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    val gridColumnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
    val showHiddenFile: Boolean = false,
)

internal sealed interface FolderTopAppBarAction {
    data object Back : FolderTopAppBarAction

    data object Search : FolderTopAppBarAction

    data object Sort : FolderTopAppBarAction

    data object FileListDisplay : FolderTopAppBarAction

    data object GridSize : FolderTopAppBarAction

    data object HiddenFile : FolderTopAppBarAction

    data object Settings : FolderTopAppBarAction
}

@Composable
internal fun FolderAppBar(
    uiState: FolderAppBarUiState,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = { BackIconButton(onClick = onBackClick) },
        actions = {
            val gridSizeItemState = rememberGridSizeItemState()
            val fileListDisplayItemState = rememberFileListDisplayItemState()
            val hiddenFilesToggleableItemState = rememberHiddenFilesToggleableItemState()
            AppBarRow2 {
                clickableItem(
                    onClick = onSearchClick,
                    icon = {
                        Icon(
                            ComicIcons.Search,
                            stringResource(Res.string.folder_action_search),
                        )
                    },
                    label = {
                        Text("Search")
                    },
                )
                clickableItem(
                    onClick = onSortClick,
                    icon = {
                        Icon(ComicIcons.SortByAlpha, "sort")
                    },
                    label = {
                        Text("Sort")
                    },
                )
                fileListDisplayItemState.fileListDisplayItem()
                gridSizeItemState.gridSizeItem()
                hiddenFilesToggleableItemState.hiddenFilesToggleableItem()
                clickableItem(
                    onClick = onSettingsClick,
                    icon = { Icon(ComicIcons.Settings, null) },
                    label = { Text(stringResource(Res.string.folder_action_settings)) },
                )
            }
        },
        modifier = modifier,
    )
}
