package com.sorrowblue.comicviewer.feature.settings.extension

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.extension.generated.resources.Res
import comicviewer.feature.settings.extension.generated.resources.settings_extension_label_image_cache
import comicviewer.feature.settings.extension.generated.resources.settings_extension_label_plugin
import comicviewer.feature.settings.extension.generated.resources.settings_extension_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ExtensionSettingsScreen(
    onBackClick: () -> Unit,
    onImageCacheClick: () -> Unit,
    onPluginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_extension_title)) },
        onBackClick = onBackClick,
        modifier = modifier,
    ) {
        Setting(
            title = {
                Text(stringResource(Res.string.settings_extension_label_image_cache))
            },
            icon = {
                Box {
                    Icon(ComicIcons.Image, null)
                    Icon(
                        ComicIcons.Cached,
                        null,
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.TopEnd)
                            .background(ListItemDefaults.colors().containerColor, CircleShape)
                    )
                }
            },
            onClick = onImageCacheClick
        )
        Setting(
            title = {
                Text(stringResource(Res.string.settings_extension_label_plugin))
            },
            icon = {
                Icon(ComicIcons.Extension, null)
            },
            onClick = onPluginClick
        )
    }
}

@Composable
@Preview
private fun ExtensionSettingsScreenPreview() {
    PreviewTheme {
        ExtensionSettingsScreen(
            onBackClick = {},
            onImageCacheClick = {},
            onPluginClick = {},
        )
    }
}
