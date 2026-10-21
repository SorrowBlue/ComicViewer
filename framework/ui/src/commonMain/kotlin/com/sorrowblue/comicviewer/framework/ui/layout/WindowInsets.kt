/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun LayoutDirection.WindowInsets(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): WindowInsets = when (this) {
    LayoutDirection.Ltr -> WindowInsets(left = start, top = top, right = end, bottom = bottom)
    LayoutDirection.Rtl -> WindowInsets(left = end, top = top, right = start, bottom = bottom)
}
