package com.sorrowblue.comicviewer.folder.section

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.BackButton
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox
import kotlinx.collections.immutable.toPersistentList
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class FolderAppBarUiState(
    val title: String = "",
    val sortItem: SortItem = SortItem.Name,
    val sortOrder: SortOrder = SortOrder.Asc,
    val fileContentType: FileContentType = FileContentType.Grid(FileContentType.GridSize.Medium),
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun FolderAppBar(
    uiState: FolderAppBarUiState,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onSortItemClick: (SortItem) -> Unit,
    onSortOrderClick: (SortOrder) -> Unit,
    onFileListChange: () -> Unit,
    onGridSizeChange: () -> Unit,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        actions = {
            PlainTooltipBox(tooltipContent = { Text(stringResource(R.string.folder_action_search)) }) {
                IconButton(onClick = onSearchClick) {
                    Icon(ComicIcons.Search, stringResource(R.string.folder_action_search))
                }
            }

            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass.widthSizeClass
            if (windowSizeClass == WindowWidthSizeClass.Compact) {
                IconButton(onClick = onSortClick) {
                    Icon(ComicIcons.SortByAlpha, "sort")
                }
            } else {
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            ComicIcons.SortByAlpha,
                            "sort"
                        )
                    }
                    val sortItems = remember {
                        SortItem.entries.toPersistentList()
                    }
                    val sortOrders = remember {
                        SortOrder.entries.toPersistentList()
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        sortItems.forEach {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = it.label)) },
                                onClick = {
                                    expanded = false
                                    onSortItemClick(it)
                                },
                                leadingIcon = {
                                    if (it == uiState.sortItem) {
                                        Icon(ComicIcons.Check, contentDescription = null)
                                    }
                                },
                                modifier = if (it == uiState.sortItem) {
                                    Modifier.background(
                                        color = ComicTheme.colorScheme.secondaryContainer
                                    )
                                } else {
                                    Modifier
                                }
                            )
                        }
                        HorizontalDivider()
                        sortOrders.forEach {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = it.label)) },
                                onClick = {
                                    expanded = false
                                    onSortOrderClick(it)
                                },
                                leadingIcon = {
                                    if (it == uiState.sortOrder) {
                                        Icon(ComicIcons.Check, contentDescription = null)
                                    }
                                },
                                modifier = if (it == uiState.sortOrder) {
                                    Modifier.background(
                                        color = ComicTheme.colorScheme.secondaryContainer
                                    )
                                } else {
                                    Modifier
                                }
                            )
                        }
                    }
                }
            }
            OverflowMenu {
                if (uiState.fileContentType is FileContentType.Grid) {
                    OverflowMenuItem(
                        text = stringResource(
                            id = com.sorrowblue.comicviewer.feature.file.R.string.file_list_label_switch_list_view
                        ),
                        icon = ComicIcons.ViewList,
                        onClick = onFileListChange
                    )
                } else {
                    OverflowMenuItem(
                        text = stringResource(
                            id = com.sorrowblue.comicviewer.feature.file.R.string.file_list_label_switch_grid_view
                        ),
                        icon = ComicIcons.GridView,
                        onClick = onFileListChange
                    )
                }
                if (uiState.fileContentType is FileContentType.Grid) {
                    OverflowMenuItem(
                        text = stringResource(R.string.folder_action_change_grid_size),
                        icon = ComicIcons.Grid4x4,
                        onClick = onGridSizeChange
                    )
                }
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
