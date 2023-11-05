package com.sorrowblue.comicviewer.folder.section

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.file.component.FileContentLayoutButton
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.PreviewComic
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveTopAppBar

internal data class FolderAppBarUiState(
    val title: String = "",
    val fileContentType: FileContentType = FileContentType.Grid(FileContentType.GridSize.Medium),
)

@Composable
internal fun FolderAppBar(
    uiState: FolderAppBarUiState,
    onBackClick: () -> Unit,
    onFileListChange: () -> Unit,
    onSearchClick: () -> Unit,
    onGridSizeChange: () -> Unit,
    onSortClick: () -> Unit,
    onSettingsClick: () -> Unit,
    windowInsets: WindowInsets,
    scrollBehavior: TopAppBarScrollBehavior,
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

            OverflowMenu {
                if (uiState.fileContentType is FileContentType.Grid) {
                    OverflowMenuItem(
                        text = "Change Grid size",
                        icon = ComicIcons.Grid4x4,
                        onClick = onGridSizeChange
                    )
                }
                OverflowMenuItem(
                    text = "Sort by",
                    icon = ComicIcons.SortByAlpha,
                    onClick = onSortClick
                )
                OverflowMenuItem(
                    text = "Settings",
                    icon = ComicIcons.Settings,
                    onClick = onSettingsClick
                )
            }
        },
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior
    )
}

@PreviewComic
@Composable
fun PreviewFolderAppBar() {
    PreviewTheme {
        FolderAppBar(
            uiState = FolderAppBarUiState(),
            onBackClick = { /*TODO*/ },
            onFileListChange = { /*TODO*/ },
            onSearchClick = { /*TODO*/ },
            onGridSizeChange = { /*TODO*/ },
            onSortClick = { /*TODO*/ },
            onSettingsClick = { /*TODO*/ },
            windowInsets = WindowInsets.safeDrawing,
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        )
    }
}
