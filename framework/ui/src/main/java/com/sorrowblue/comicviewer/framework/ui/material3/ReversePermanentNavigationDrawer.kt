package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

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

private fun LayoutDirection.reverse() = when (this) {
    LayoutDirection.Ltr -> LayoutDirection.Rtl
    LayoutDirection.Rtl -> LayoutDirection.Ltr
}
