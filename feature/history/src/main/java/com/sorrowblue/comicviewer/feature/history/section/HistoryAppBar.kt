package com.sorrowblue.comicviewer.feature.history.section

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ClearAll
import androidx.compose.material.icons.twotone.Grid4x4
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.history.R
import com.sorrowblue.comicviewer.file.component.FileContentLayout
import com.sorrowblue.comicviewer.file.component.FileContentLayoutButton
import com.sorrowblue.comicviewer.framework.compose.material3.OverflowMenu
import com.sorrowblue.comicviewer.framework.compose.material3.rememberOverflowMenuState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryAppBar(
    fileContentLayout: FileContentLayout,
    topAppBarScrollBehavior: TopAppBarScrollBehavior? = null,
    onFileContentLayoutClick: () -> Unit = {},
    onGridSizeClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(stringResource(R.string.history_title)) },
        actions = {
            FileContentLayoutButton(fileContentLayout = fileContentLayout, onClick = onFileContentLayoutClick)
            val overflowMenuState = rememberOverflowMenuState()
            OverflowMenu(overflowMenuState) {
                if (fileContentLayout is FileContentLayout.Grid) {
                    DropdownMenuItem(
                        text = { Text(text = "Change Grid size") },
                        trailingIcon = { Icon(Icons.TwoTone.Grid4x4, "Change grid size") },
                        onClick = {
                            overflowMenuState.collapse()
                            onGridSizeClick()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text(stringResource(com.sorrowblue.comicviewer.framework.resource.R.string.framework_title_settings)) },
                    trailingIcon = {
                        Icon(
                            Icons.TwoTone.Settings,
                            stringResource(com.sorrowblue.comicviewer.framework.resource.R.string.framework_title_settings)
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
