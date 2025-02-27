package com.sorrowblue.comicviewer.framework.ui.core

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun isCompactWindowClass(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT ||
        windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.Companion.COMPACT
}

@Composable
fun isLandscape(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return false
    // TODO()
}
