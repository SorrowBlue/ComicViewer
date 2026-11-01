/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode

val LocalDarkMode = staticCompositionLocalOf { DarkMode.DEVICE }

@ReadOnlyComposable
@Composable
internal actual fun colorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme =
    when (LocalDarkMode.current) {
        DarkMode.DEVICE -> when {
            darkTheme -> darkScheme
            else -> lightScheme
        }

        DarkMode.DARK -> darkScheme

        DarkMode.LIGHT -> lightScheme
    }
