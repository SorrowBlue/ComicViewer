package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
fun PaddingValues.asWindowInsets(localLayoutDirection: LayoutDirection = LocalLayoutDirection.current) =
    WindowInsets(
        left = calculateLeftPadding(localLayoutDirection),
        top = calculateTopPadding(),
        right = calculateRightPadding(localLayoutDirection),
        bottom = calculateBottomPadding()
    )

@Composable
fun PaddingValues.add(
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

@Composable
fun PaddingValues.copy(
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    start: Dp = calculateStartPadding(layoutDirection),
    top: Dp = calculateTopPadding(),
    end: Dp = calculateEndPadding(layoutDirection),
    bottom: Dp = calculateBottomPadding(),
) = PaddingValues(start = start, top = top, end = end, bottom = bottom)

@Composable
fun Modifier.marginPadding(
    horizontal: Boolean = false,
    vertical: Boolean = false,
    start: Boolean = false,
    top: Boolean = false,
    end: Boolean = false,
    bottom: Boolean = false,
) = with(this) {
    padding(
        start = if (horizontal || start) ComicTheme.dimension.margin else 0.dp,
        top = if (vertical || top) ComicTheme.dimension.margin else 0.dp,
        end = if (horizontal || end) ComicTheme.dimension.margin else 0.dp,
        bottom = if (vertical || bottom) ComicTheme.dimension.margin else 0.dp
    )
}

@Composable
fun calculatePaddingMargins(contentPadding: PaddingValues): Pair<PaddingValues, PaddingValues> {
    val isCompact = LocalWindowSize.current.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    val paddings = if (isCompact) contentPadding.copy(
        top = 0.dp,
        bottom = 0.dp
    ) else contentPadding.copy(top = 0.dp)
    val margins = if (isCompact) PaddingValues(
        top = contentPadding.calculateTopPadding(),
        bottom = contentPadding.calculateBottomPadding()
    ) else PaddingValues(
        top = contentPadding.calculateTopPadding(),
    )
    return paddings to margins
}
