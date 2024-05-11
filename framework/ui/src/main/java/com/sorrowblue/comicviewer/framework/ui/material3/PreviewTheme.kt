package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.calculatePosture
import androidx.compose.material3.adaptive.collectFoldingFeaturesAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.core.layout.WindowSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalWindowAdaptiveInfo
import com.sorrowblue.comicviewer.framework.ui.LocalWindowSize

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    val windowSizeClass = WindowSizeClass.compute(
        configuration.screenWidthDp.toFloat(),
        configuration.screenHeightDp.toFloat()
    )
    CompositionLocalProvider(
        LocalWindowSize provides windowSizeClass,
        LocalWindowAdaptiveInfo provides WindowAdaptiveInfo(
            windowSizeClass,
            calculatePosture(collectFoldingFeaturesAsState().value)
        )
    ) {
        ComicTheme(useDynamicColor = true) {
            content()
        }
    }
}
