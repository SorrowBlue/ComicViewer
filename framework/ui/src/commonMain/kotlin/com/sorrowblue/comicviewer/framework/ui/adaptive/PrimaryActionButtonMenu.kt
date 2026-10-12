/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp

/**
 * [FloatingActionButtonMenu] has internal padding (horizontal: 16.dp, bottom: 16.dp) hardcoded
 * in Material 3. This modifier adjusts the layout measurement and placement so that the
 * toggle button perfectly aligns with regular [androidx.compose.material3.FloatingActionButton].
 */
fun Modifier.removeFloatingActionButtonMenuPadding(): Modifier = layout { measurable, constraints ->
    val horizontalSidePadding = 16.dp.roundToPx()
    val horizontalPadding = horizontalSidePadding * 2
    val bottomPadding = 16.dp.roundToPx()
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = 0,
            minHeight = 0,
            maxWidth = if (constraints.hasBoundedWidth) {
                (constraints.maxWidth + horizontalPadding).coerceAtLeast(0)
            } else {
                constraints.maxWidth
            },
            maxHeight = if (constraints.hasBoundedHeight) {
                (constraints.maxHeight + bottomPadding).coerceAtLeast(0)
            } else {
                constraints.maxHeight
            },
        ),
    )
    val width = (placeable.width - horizontalPadding).coerceIn(
        constraints.minWidth,
        constraints.maxWidth,
    )
    val height = (placeable.height - bottomPadding).coerceIn(
        constraints.minHeight,
        constraints.maxHeight,
    )
    layout(width, height) {
        placeable.placeRelative(-horizontalSidePadding, 0)
    }
}
