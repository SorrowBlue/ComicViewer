package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection


@Composable
fun PaddingValues.toWindowInsets(localLayoutDirection: LayoutDirection = LocalLayoutDirection.current) =
    WindowInsets(
        left = calculateLeftPadding(localLayoutDirection),
        top = calculateTopPadding(),
        right = calculateRightPadding(localLayoutDirection),
        bottom = calculateBottomPadding()
    )

@Composable
fun PaddingValues.plus(
    paddingValues: PaddingValues,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
): PaddingValues {
    return PaddingValues(
        start = calculateStartPadding(layoutDirection) + paddingValues.calculateStartPadding(
            layoutDirection
        ),
        top = calculateTopPadding() + paddingValues.calculateTopPadding(),
        end = calculateEndPadding(layoutDirection) + paddingValues.calculateEndPadding(
            layoutDirection
        ),
        bottom = calculateBottomPadding() + paddingValues.calculateBottomPadding(),
    )
}
