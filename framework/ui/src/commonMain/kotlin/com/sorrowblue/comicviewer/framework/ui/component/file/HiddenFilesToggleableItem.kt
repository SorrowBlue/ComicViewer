/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.component.file

import androidx.compose.material3.AppBarRowScope
import androidx.compose.material3.Icon
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.toggleableItem
import comicviewer.framework.ui.generated.resources.Res
import comicviewer.framework.ui.generated.resources.file_action_show_hidden
import org.jetbrains.compose.resources.stringResource

context(scope: AppBarRowScope)
fun HiddenFilesToggleableItem(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    scope.toggleableItem(
        checked = checked,
        onCheckedChange = onCheckedChange,
        icon = {
            Icon(ComicIcons.RemoveRedEye, null)
        },
        label = {
            stringResource(Res.string.file_action_show_hidden)
        },
    )
}
