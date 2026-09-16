/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.component.file

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AppBarRowScope
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.clickableItem
import comicviewer.framework.ui.generated.resources.Res
import comicviewer.framework.ui.generated.resources.file_list_label_switch_grid_view
import comicviewer.framework.ui.generated.resources.file_list_label_switch_list_view
import org.jetbrains.compose.resources.stringResource

@Composable
fun FileListDisplayItem(
    fileListDisplay: FileListDisplay,
    onDisplayChange: (FileListDisplay) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        modifier = modifier,
    ) {
        FileListDisplay.entries.forEachIndexed { index, option ->
            TonalToggleButton(
                colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                checked = option == fileListDisplay,
                onCheckedChange = {
                    if (it) {
                        onDisplayChange(option)
                    }
                },
                modifier = Modifier.semantics { role = Role.RadioButton },
                shapes =
                    when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        FileListDisplay.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
            ) {
                Icon(
                    imageVector = when (option) {
                        FileListDisplay.Grid -> ComicIcons.GridView
                        FileListDisplay.List -> ComicIcons.ViewList
                    },
                    contentDescription = null,
                )
            }
        }
    }
}

context(scope: AppBarRowScope)
fun FileListDisplayItem(fileListDisplay: FileListDisplay, onClick: () -> Unit) {
    scope.clickableItem(
        onClick = onClick,
        icon = {
            Icon(
                if (fileListDisplay == FileListDisplay.Grid) {
                    ComicIcons.ViewList
                } else {
                    ComicIcons.GridView
                },
                null,
            )
        },
        label = {
            if (fileListDisplay == FileListDisplay.Grid) {
                stringResource(Res.string.file_list_label_switch_list_view)
            } else {
                stringResource(Res.string.file_list_label_switch_grid_view)
            }
        },
    )
}
