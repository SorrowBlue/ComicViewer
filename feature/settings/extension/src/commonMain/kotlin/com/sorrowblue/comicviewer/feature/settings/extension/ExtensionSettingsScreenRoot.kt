package com.sorrowblue.comicviewer.feature.settings.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
internal fun ExtensionSettingsScreenRoot(onBackClick: () -> Unit, onImageCacheClick: () -> Unit) {
    ExtensionSettingsScreen(
        onBackClick = onBackClick,
        onImageCacheClick = onImageCacheClick,
        modifier = Modifier.testTag("ExtensionSettingsRoot"),
    )
}
