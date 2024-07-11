package com.sorrowblue.comicviewer.folder.section

import android.os.Parcelable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.file.component.ChangeGridSize
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.file.component.rememberFileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.BackButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderAppBar(
    uiState: FolderAppBarUiState,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onFileListChange: () -> Unit,
    onGridSizeChange: () -> Unit,
    onHideFileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    TopAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = { BackButton(onClick = onBackClick) },
        actions = {
            PlainTooltipBox(tooltipContent = { Text(stringResource(R.string.folder_action_search)) }) {
                IconButton(onClick = onSearchClick) {
                    Icon(ComicIcons.Search, stringResource(R.string.folder_action_search))
                }
            }
            IconButton(onClick = onSortClick) {
                Icon(ComicIcons.SortByAlpha, "sort")
            }
            val fileContentType by rememberFileContentType(
                fileListDisplay = uiState.fileListDisplay,
                gridColumnSize = uiState.gridColumnSize
            )
            OverflowMenu {
                FileContentType(
                    fileContentType = fileContentType,
                    onClick = onFileListChange
                )
                ChangeGridSize(fileContentType = fileContentType, onClick = onGridSizeChange)
                ShowHiddenFIle(showHiddenFile = uiState.showHiddenFile, onClick = onHideFileClick)
                OverflowMenuItem(
                    text = stringResource(R.string.folder_action_settings),
                    icon = ComicIcons.Settings,
                    onClick = onSettingsClick
                )
            }
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun OverflowMenuScope.ShowHiddenFIle(showHiddenFile: Boolean, onClick: () -> Unit) {
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
