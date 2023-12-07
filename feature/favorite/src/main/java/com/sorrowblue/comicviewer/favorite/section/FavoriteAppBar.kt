package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.file.component.FileContentLayoutButton
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenuItem
import com.sorrowblue.comicviewer.framework.ui.material3.PlainTooltipBox
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.material3.rememberOverflowMenuState
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveTopAppBar

internal data class FavoriteAppBarUiState(
    val title: String = "",
    val fileContentType: FileContentType = FileContentType.Grid(),
)

@Composable
internal fun FavoriteAppBar(
    uiState: FavoriteAppBarUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFileListTypeChange: () -> Unit,
    onGridSizeChange: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    ResponsiveTopAppBar(
        title = uiState.title,
        onBackClick = onBackClick,
        actions = {
            PlainTooltipBox(tooltipContent = { Text(stringResource(R.string.favorite_title_edit)) }) {
                IconButton(onClick = onEditClick) {
                    Icon(ComicIcons.Edit, stringResource(R.string.favorite_title_edit))
                }
            }

            FileContentLayoutButton(
                fileContentType = uiState.fileContentType,
                onFileListTypeChange
            )

            OverflowMenu(state = rememberOverflowMenuState()) {
                if (uiState.fileContentType is FileContentType.Grid) {
                    OverflowMenuItem(
                        text = stringResource(
                            id = com.sorrowblue.comicviewer.feature.folder.R.string.folder_action_change_grid_size
                        ),
                        icon = ComicIcons.Grid4x4,
                        onClick = onGridSizeChange
                    )
                }
                OverflowMenuItem(
                    text = stringResource(R.string.favorite_action_delete),
                    icon = ComicIcons.Delete,
                    onClick = onDeleteClick
                )
                OverflowMenuItem(
                    text = stringResource(
                        id = com.sorrowblue.comicviewer.framework.ui.R.string.framework_ui_label_settings
                    ),
                    icon = ComicIcons.Settings,
                    onClick = onSettingsClick
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}
