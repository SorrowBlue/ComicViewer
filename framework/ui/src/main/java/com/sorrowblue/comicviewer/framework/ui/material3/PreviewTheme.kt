package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    CompositionLocalProvider(
        LocalWindowSize provides WindowSizeClass.calculateFromSize(
            DpSize(
                configuration.screenWidthDp.dp,
                configuration.screenHeightDp.dp
            )
        )
    ) {
        ComicTheme(useDynamicColor = true) {
            content()
        }
    }
}
