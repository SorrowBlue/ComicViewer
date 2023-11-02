package com.sorrowblue.comicviewer.folder.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.file.component.FileContentLayoutButton
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuState
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.material3.rememberOverflowMenuState
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveTopAppBar

data class FolderAppBarUiState(
    val title: String = "",
    val fileContentType: FileContentType = FileContentType.Grid(FileContentType.GridSize.Medium),
)

@Composable
fun FolderAppBar(
    uiState: FolderAppBarUiState = FolderAppBarUiState(),
    onFileListChange: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onGridSizeChange: () -> Unit = {},
    onSortClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    overflowMenuState: OverflowMenuState = rememberOverflowMenuState(),
) {
    ResponsiveTopAppBar(
        title = uiState.title,
        onBackClick = onBackClick,
        actions = {
            PlainTooltipBox(tooltipContent = { Text("Search") }) {
                IconButton(onClick = onSearchClick) {
                    Icon(ComicIcons.Search, "Search")
                }
            }

            FileContentLayoutButton(uiState.fileContentType, onFileListChange)

            OverflowMenu(overflowMenuState) {
                if (uiState.fileContentType is FileContentType.Grid) {
                    DropdownMenuItem(
                        text = { Text(text = "Change Grid size") },
                        leadingIcon = { Icon(ComicIcons.Grid4x4, "Change grid size") },
                        onClick = {
                            overflowMenuState.collapse()
                            onGridSizeChange()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text(text = "Sort by") },
                    leadingIcon = { Icon(ComicIcons.SortByAlpha, "Sort by") },
                    onClick = {
                        overflowMenuState.collapse()
                        onSortClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "Settings") },
                    leadingIcon = { Icon(ComicIcons.Settings, "Settings") },
                    onClick = {
                        overflowMenuState.collapse()
                        onSettingsClick()
                    }
                )
            }
        },
        windowInsets = paddingValues.asWindowInsets(),
        scrollBehavior = scrollBehavior
    )
}

@Preview
@Composable
fun PreviewFolderAppBar() {
    ComicTheme {
        FolderAppBar()
    }
}
