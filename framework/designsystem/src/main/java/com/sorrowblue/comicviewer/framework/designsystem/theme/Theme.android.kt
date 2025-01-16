package com.sorrowblue.comicviewer.framework.designsystem.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
actual fun ComicTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val dimension by remember(windowSizeClass.windowWidthSizeClass) {
        mutableStateOf(
            when (windowSizeClass.windowWidthSizeClass) {
                WindowWidthSizeClass.COMPACT -> compactDimension
                WindowWidthSizeClass.MEDIUM -> mediumDimension
                WindowWidthSizeClass.EXPANDED -> expandedDimension
                else -> compactDimension
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
