/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.window.core.layout.WindowSizeClass

object ComicTheme {
    val motionScheme: MotionScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.motionScheme
    val dimension: Dimension
        @Composable
        @ReadOnlyComposable
        get() {
            return LocalDimension.current
        }

    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes
}

@Composable
fun ComicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = colorScheme(darkTheme, dynamicColor)
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val dimension = when {
        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
        ) -> expandedDimension

        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
        ) -> mediumDimension

        else -> compactDimension
    }
    CompositionLocalProvider(LocalDimension provides dimension) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}

private val AppTypography = Typography()
