/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.component.file

import androidx.compose.material3.AppBarRowScope
import androidx.compose.material3.Icon
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.clickableItem
import comicviewer.framework.ui.generated.resources.Res
import comicviewer.framework.ui.generated.resources.file_action_change_grid_size
import org.jetbrains.compose.resources.stringResource

context(scope: AppBarRowScope)
fun GridSizeItem(fileListDisplay: FileListDisplay, onClick: () -> Unit) {
    if (fileListDisplay == FileListDisplay.Grid) {
        scope.clickableItem(
            icon = { Icon(ComicIcons.Grid4x4, null) },
            label = { stringResource(Res.string.file_action_change_grid_size) },
            onClick = onClick,
        )
    }
}
