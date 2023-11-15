package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize

@Composable
fun ReversePermanentNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection.reverse()) {
        PermanentNavigationDrawer(
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    drawerContent()
                }
            },
            modifier
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                content()
            }
        }
    }
}

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
        ComicTheme {
            content()
        }
    }
}

private fun LayoutDirection.reverse() = when (this) {
    LayoutDirection.Ltr -> LayoutDirection.Rtl
    LayoutDirection.Rtl -> LayoutDirection.Ltr
}
