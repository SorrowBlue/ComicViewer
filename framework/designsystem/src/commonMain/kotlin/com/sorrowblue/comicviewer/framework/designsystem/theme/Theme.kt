package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowSizeClass

@Composable
fun ComicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = colorScheme(darkTheme, dynamicColor)
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val dimension by remember(windowSizeClass) {
        mutableStateOf(
            if (windowSizeClass.containsHeightDp(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
                expandedDimension
            } else if (windowSizeClass.containsHeightDp(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                mediumDimension
            } else {
                compactDimension
            }
        )
    }
    CompositionLocalProvider(LocalDimension provides dimension) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

@Composable
internal expect fun colorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme


internal val AppTypography = Typography()
