package com.sorrowblue.comicviewer.folder.section

import android.os.Parcelable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.file.component.FileListDisplayItem
import com.sorrowblue.comicviewer.file.component.GridSizeItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalTopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuScope
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class FolderAppBarUiState(
    val title: String = "",
    val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    val gridColumnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
    val showHiddenFile: Boolean = false,
) : Parcelable

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
    onAction: (FolderTopAppBarAction) -> Unit,
    scrollableState: ScrollableState,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    CanonicalTopAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = { BackIconButton(onClick = { onAction(FolderTopAppBarAction.Back) }) },
        actions = {
            PlainTooltipBox(tooltipContent = { Text(stringResource(R.string.folder_action_search)) }) {
                IconButton(onClick = { onAction(FolderTopAppBarAction.Search) }) {
                    Icon(ComicIcons.Search, stringResource(R.string.folder_action_search))
                }
            }
            IconButton(onClick = { onAction(FolderTopAppBarAction.Sort) }) {
                Icon(ComicIcons.SortByAlpha, "sort")
            }
            OverflowMenu {
                FileListDisplayItem(
                    fileListDisplay = uiState.fileListDisplay,
                    onClick = { onAction(FolderTopAppBarAction.FileListDisplay) }
                )
                GridSizeItem(
                    fileListDisplay = uiState.fileListDisplay,
                    onClick = { onAction(FolderTopAppBarAction.GridSize) }
                )
                ShowHiddenFile(
                    showHiddenFile = uiState.showHiddenFile,
                    onClick = { onAction(FolderTopAppBarAction.HiddenFile) }
                )
                OverflowMenuItem(
                    text = stringResource(R.string.folder_action_settings),
                    icon = ComicIcons.Settings,
                    onClick = { onAction(FolderTopAppBarAction.Settings) }
                )
            }
        },
        scrollBehavior = scrollBehavior,
        scrollableState = scrollableState,
        modifier = modifier
    )
}

@Composable
private fun OverflowMenuScope.ShowHiddenFile(showHiddenFile: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(text = stringResource(R.string.folder_action_show_hidden)) },
        leadingIcon = {
            Icon(imageVector = ComicIcons.FolderOff, contentDescription = null)
        },
        trailingIcon = {
            Checkbox(
                checked = showHiddenFile,
                onCheckedChange = {
                    onClick()
                    state.collapse()
                }
            )
        },
        onClick = {
            onClick()
            state.collapse()
        }
    )
}
