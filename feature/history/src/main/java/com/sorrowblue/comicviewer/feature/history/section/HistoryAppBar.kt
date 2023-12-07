package com.sorrowblue.comicviewer.feature.history.section

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.history.R
import com.sorrowblue.comicviewer.file.component.FileContentLayoutButton
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.ui.material3.rememberOverflowMenuState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryAppBar(
    fileContentType: FileContentType,
    topAppBarScrollBehavior: TopAppBarScrollBehavior? = null,
    onFileContentLayoutClick: () -> Unit = {},
    onGridSizeClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    TopAppBar(
        title = { Text(stringResource(R.string.history_title)) },
        actions = {
            FileContentLayoutButton(
                fileContentType = fileContentType,
                onClick = onFileContentLayoutClick
            )
            val overflowMenuState = rememberOverflowMenuState()
            OverflowMenu(state = overflowMenuState) {
                if (fileContentType is FileContentType.Grid) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.history_action_grid_size)) },
                        trailingIcon = {
                            Icon(
                                ComicIcons.Grid4x4,
                                stringResource(R.string.history_action_grid_size)
                            )
                        },
                        onClick = {
                            overflowMenuState.collapse()
                            onGridSizeClick()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text(stringResource(id = com.sorrowblue.comicviewer.framework.ui.R.string.framework_ui_label_settings)) },
                    trailingIcon = {
                        Icon(
                            ComicIcons.Settings,
                            stringResource(id = com.sorrowblue.comicviewer.framework.ui.R.string.framework_ui_desc_open_settings)
                        )
                    },
                    onClick = {
                        overflowMenuState.collapse()
                        onSettingsClick()
                    }
                )
            }
        },
        scrollBehavior = topAppBarScrollBehavior
    )
}
