package com.sorrowblue.comicviewer.framework.preview

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.calculatePosture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.compactDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.expandedDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.mediumDimension

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    ComicTheme {
        val configuration = LocalConfiguration.current
        val windowSizeClass = WindowSizeClass.compute(
            configuration.screenWidthDp.toFloat(),
            configuration.screenHeightDp.toFloat()
        )
        val windowAdaptiveInfo = WindowAdaptiveInfo(windowSizeClass, calculatePosture(emptyList()))
        val dimension by remember(windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass) {
            mutableStateOf(
                when (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass) {
                    WindowWidthSizeClass.COMPACT -> compactDimension
                    WindowWidthSizeClass.MEDIUM -> mediumDimension
                    WindowWidthSizeClass.EXPANDED -> expandedDimension
                    else -> compactDimension
                }
            )
        }
        CompositionLocalProvider(LocalDimension provides dimension) {
            content()
        }
    }
}
