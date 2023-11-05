package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
fun ListItemSwitch(
    headlineContent: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = if (enabled) {
        ListItemDefaults.colors()
    } else {
        ListItemDefaults.colors(
            headlineColor = ComicTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            leadingIconColor = ComicTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            overlineColor = ComicTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            supportingColor = ComicTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            trailingIconColor = ComicTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        )
    },
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
) {
    Box(
        modifier = modifier,
    ) {
        ListItem(
            headlineContent = headlineContent,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = {
                Switch(checked = checked, onCheckedChange = {}, enabled = enabled)
            },
            colors = colors,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation,
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable(enabled = enabled, onClick = { onCheckedChange.invoke(!checked) })
        )
    }
}
