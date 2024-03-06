package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.ui.LocalWindowSize

@Composable
fun rememberMobile(): Boolean {
    val windowSize = LocalWindowSize.current
    return remember(windowSize) {
        windowSize.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    }
}
