package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.window.core.layout.WindowSizeClass

val LocalWindowSize =
    staticCompositionLocalOf {
        WindowSizeClass(0, 0)
    }
