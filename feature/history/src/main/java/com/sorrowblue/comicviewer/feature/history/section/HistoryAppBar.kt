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
            OverflowMenu(overflowMenuState) {
                if (fileContentType is FileContentType.Grid) {
                    DropdownMenuItem(
                        text = { Text(text = "Change Grid size") },
                        trailingIcon = { Icon(ComicIcons.Grid4x4, "Change grid size") },
                        onClick = {
                            overflowMenuState.collapse()
                            onGridSizeClick()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Open Settings") },
                    trailingIcon = {
                        Icon(ComicIcons.Settings, "Open Settings")
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
