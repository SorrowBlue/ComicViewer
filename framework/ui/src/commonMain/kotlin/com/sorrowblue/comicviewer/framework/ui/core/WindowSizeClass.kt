package com.sorrowblue.comicviewer.framework.ui.core

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass

@Composable
fun isCompactWindowClass(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
        !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
}

@Composable
fun DetectOrientation(
    modifier: Modifier = Modifier,
    content: @Composable BoxWithConstraintsScope.(isPortrait: Boolean) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        content(height < width)
    }
}
