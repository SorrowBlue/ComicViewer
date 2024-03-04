package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.window.core.layout.WindowSizeClass

val LocalWindowSize =
    staticCompositionLocalOf {
        WindowSizeClass(0, 0)
    }
