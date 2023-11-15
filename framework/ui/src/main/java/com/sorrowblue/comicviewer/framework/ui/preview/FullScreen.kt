package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize

@Composable
fun rememberMobile(): Boolean {
    val windowSize = LocalWindowSize.current
    return remember(windowSize) {
        windowSize.widthSizeClass == WindowWidthSizeClass.Compact
    }
}
