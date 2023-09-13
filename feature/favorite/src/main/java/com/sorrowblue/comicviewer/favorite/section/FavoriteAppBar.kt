package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Grid4x4
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.file.component.FileContentLayout
import com.sorrowblue.comicviewer.file.component.FileContentLayoutButton
import com.sorrowblue.comicviewer.framework.compose.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.compose.material3.OverflowMenuState
import com.sorrowblue.comicviewer.framework.compose.material3.rememberOverflowMenuState

internal data class FavoriteAppBarUiState(
    val title: String = "",
    val fileContentLayout: FileContentLayout = FileContentLayout.Grid()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteAppBar(
    uiState: FavoriteAppBarUiState = FavoriteAppBarUiState(),
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onFileListTypeChange: () -> Unit = {},
    onGridSizeChange: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    overflowMenuState: OverflowMenuState = rememberOverflowMenuState()
) {
    TopAppBar(
        title = { Text(text = uiState.title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.TwoTone.ArrowBack, "Back")
            }
        },
        actions = {
            PlainTooltipBox(tooltip = { Text(stringResource(R.string.favorite_title_edit)) }) {
                IconButton(onClick = onEditClick, modifier = Modifier.tooltipAnchor()) {
                    Icon(Icons.TwoTone.Edit, stringResource(R.string.favorite_title_edit))
                }
            }

            FileContentLayoutButton(
                fileContentLayout = uiState.fileContentLayout,
                onFileListTypeChange
            )

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
                    text = { Text(text = "Delete") },
                    leadingIcon = { Icon(Icons.TwoTone.Delete, "Remove Favorite") },
                    onClick = {
                        overflowMenuState.collapse()
                        onDeleteClick()
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
        scrollBehavior = scrollBehavior
    )
}
