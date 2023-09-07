package com.sorrowblue.comicviewer.framework.compose.material3

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun ListItem2(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = if (enabled) ListItemDefaults.colors() else ListItemDefaults.colors(
        headlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        leadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        overlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        supportingColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        trailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    ),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        modifier = modifier,
    )
}
