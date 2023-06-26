package com.sorrowblue.comicviewer.framework.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max

@Composable
fun PaddingValues.copy(all: Dp) = PaddingValues(
    start = calculateStartPadding(LocalLayoutDirection.current) + all,
    top = calculateTopPadding() + all,
    end = calculateEndPadding(LocalLayoutDirection.current) + all,
    bottom = calculateBottomPadding() + all
)

@Composable
fun PaddingValues.copy(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
): PaddingValues = PaddingValues(
    start = calculateStartPadding(LocalLayoutDirection.current) + start,
    top = calculateTopPadding() + top,
    end = calculateEndPadding(LocalLayoutDirection.current) + end,
    bottom = calculateBottomPadding() + bottom
)

@Composable
fun PaddingValues.copy(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): PaddingValues = PaddingValues(
    horizontal = max(
        calculateStartPadding(LocalLayoutDirection.current),
        calculateEndPadding(LocalLayoutDirection.current)
    ) + horizontal,
    vertical = max(calculateTopPadding(), calculateBottomPadding()) + vertical
)
