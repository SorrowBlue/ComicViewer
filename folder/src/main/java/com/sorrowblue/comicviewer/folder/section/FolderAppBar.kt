package com.sorrowblue.comicviewer.folder.section

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Grid4x4
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.SortByAlpha
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.file.FileListTypeIconButton2
import com.sorrowblue.comicviewer.file.component.FileContentLayout
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.compose.material3.OverflowMenuState
import com.sorrowblue.comicviewer.framework.compose.material3.rememberOverflowMenuState

data class FolderAppBarUiState(
    val title: String = "",
    val fileContentLayout: FileContentLayout = FileContentLayout.Grid(FileContentLayout.GridSize.Medium),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderAppBar(
    uiState: FolderAppBarUiState = FolderAppBarUiState(),
    onFileListChange: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onGridSizeChange: () -> Unit = {},
    onSortClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    overflowMenuState: OverflowMenuState = rememberOverflowMenuState(),
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = { Text(text = uiState.title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.TwoTone.ArrowBack, "Back")
            }
        },
        actions = {
            PlainTooltipBox(tooltip = { Text("Search") }) {
                IconButton(onClick = onSearchClick, modifier = Modifier.tooltipAnchor()) {
                    Icon(Icons.TwoTone.Search, "Search")
                }
            }

            FileListTypeIconButton2(uiState.fileContentLayout, onFileListChange)

            OverflowMenu(overflowMenuState) {
                if (uiState.fileContentLayout is FileContentLayout.Grid) {
                    DropdownMenuItem(
                        text = { Text(text = "Change Grid size") },
                        leadingIcon = { Icon(Icons.TwoTone.Grid4x4, "Change grid size") },
                        onClick = {
                            overflowMenuState.collapse()
                            onGridSizeChange()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text(text = "Sort by") },
                    leadingIcon = { Icon(Icons.TwoTone.SortByAlpha, "Sort by") },
                    onClick = {
                        overflowMenuState.collapse()
                        onSortClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "Refresh") },
                    leadingIcon = { Icon(Icons.TwoTone.Refresh, "Refresh") },
                    onClick = {
                        overflowMenuState.collapse()
                        onRefreshClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "Settings") },
                    leadingIcon = { Icon(Icons.TwoTone.Settings, "Settings") },
                    onClick = {
                        overflowMenuState.collapse()
                        onSettingsClick()
                    }
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewFolderAppBar() {
    AppMaterialTheme {
        FolderAppBar()
    }
}
