package com.sorrowblue.comicviewer.feature.settings.plugin

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons.FilePdf
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.plugin.generated.resources.Res
import comicviewer.feature.settings.plugin.generated.resources.settings_plugin_label_disable
import comicviewer.feature.settings.plugin.generated.resources.settings_plugin_label_enable
import comicviewer.feature.settings.plugin.generated.resources.settings_plugin_title
import comicviewer.feature.settings.plugin.generated.resources.settings_plugin_title_pdf
import org.jetbrains.compose.resources.stringResource

internal data class PluginScreenUiState(val isPdfPluginEnable: Boolean = false)

@Composable
internal fun PluginScreen(
    uiState: PluginScreenUiState,
    onBackClick: () -> Unit,
    onPdfPluginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_plugin_title)) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBackClick = onBackClick,
        modifier = modifier,
    ) {
        Setting(
            title = { Text(text = stringResource(Res.string.settings_plugin_title_pdf)) },
            summary = {
                Text(
                    text = stringResource(
                        if (uiState.isPdfPluginEnable) Res.string.settings_plugin_label_enable else Res.string.settings_plugin_label_disable,
                    ),
                )
            },
            icon = {
                Icon(ComicIcons.FilePdf, null, modifier = Modifier.size(24.dp))
            },
            onClick = {},
            widget = {
                IconButton(onClick = {
                    onPdfPluginClick()
                }) {
                    Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                }
            },
        )
    }
}

@Preview
@Composable
private fun PreviewPluginScreen() {
    PreviewTheme {
        PluginScreen(
            uiState = PluginScreenUiState(),
            onBackClick = {},
            onPdfPluginClick = {},
        )
    }
}
