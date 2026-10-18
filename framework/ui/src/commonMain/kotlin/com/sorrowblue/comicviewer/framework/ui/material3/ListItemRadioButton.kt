/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ListItemRadioButton(
    headlineContent: @Composable () -> Unit,
    selected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = if (enabled) {
        ListItemDefaults.colors()
    } else {
        ListItemDefaults.colors(
            headlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            leadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            overlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            supportingColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            trailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        )
    },
    elevation: ListItemElevation = ListItemDefaults.elevation(),
) {
    Box(
        modifier = modifier,
    ) {
        ListItem(
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = {
                RadioButton(selected = selected, onClick = { })
            },
            trailingContent = trailingContent,
            colors = colors,
            elevation = elevation,
            content = headlineContent,
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable(enabled = enabled, onClick = { onCheckedChange.invoke(!selected) }),
        )
    }
}
