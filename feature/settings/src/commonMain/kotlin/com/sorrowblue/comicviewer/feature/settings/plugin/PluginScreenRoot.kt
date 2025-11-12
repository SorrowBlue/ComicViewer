package com.sorrowblue.comicviewer.feature.settings.plugin

import androidx.compose.runtime.Composable

@Composable
fun PluginScreenRoot(onBackClick: () -> Unit) {
    PluginScreen(
        onBackClick = onBackClick,
    )
}
