package com.sorrowblue.comicviewer.folder.section

import androidx.compose.material3.AppBarRow2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.file.component.fileListDisplayItem
import com.sorrowblue.comicviewer.file.component.gridSizeItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.canonical.CanonicalAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import comicviewer.feature.folder.generated.resources.Res
import comicviewer.feature.folder.generated.resources.folder_btn_search
import comicviewer.feature.folder.generated.resources.folder_btn_sort
import org.jetbrains.compose.resources.stringResource
import comicviewer.feature.folder.generated.resources.folder_action_search
import comicviewer.feature.folder.generated.resources.folder_action_settings
import comicviewer.feature.folder.generated.resources.folder_action_show_hidden
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
internal fun CanonicalScaffoldState<*>.FolderAppBar(
    uiState: FolderAppBarUiState,
    onAction: (FolderTopAppBarAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    CanonicalAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = { BackIconButton(onClick = { onAction(FolderTopAppBarAction.Back) }) },
        actions = {
            AppBarRow2 {
                clickableItem(
                    onClick = { onAction(FolderTopAppBarAction.Search) },
                    icon = {
                        Icon(
                            ComicIcons.Search,
                            stringResource(Res.string.folder_action_search)
                        )
                    },
                    label = {
                        Text(stringResource(Res.string.folder_btn_search))
                    }
                )
                clickableItem(
                    onClick = { onAction(FolderTopAppBarAction.Sort) },
                    icon = {
                        Icon(ComicIcons.SortByAlpha, "sort")
                    },
                    label = {
                        Text(stringResource(Res.string.folder_btn_sort))
                    }
                )
                fileListDisplayItem(
                    fileListDisplay = uiState.fileListDisplay,
                    onClick = { onAction(FolderTopAppBarAction.FileListDisplay) }
                )
                gridSizeItem(
                    fileListDisplay = uiState.fileListDisplay,
                    onClick = { onAction(FolderTopAppBarAction.GridSize) }
                )
                toggleableItem(
                    checked = uiState.showHiddenFile,
                    autoDismiss = false,
                    onCheckedChange = { onAction(FolderTopAppBarAction.HiddenFile) },
                    icon = { Icon(ComicIcons.FolderOff, null) },
                    label = {
                        Text(stringResource(Res.string.folder_action_show_hidden))
                    }
                )
                clickableItem(
                    onClick = { onAction(FolderTopAppBarAction.Settings) },
                    icon = { Icon(ComicIcons.Settings, null) },
                    label = { Text(stringResource(Res.string.folder_action_settings)) }
                )
            }
        },
        modifier = modifier
    )
}
