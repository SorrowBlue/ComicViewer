package com.sorrowblue.comicviewer.feature.settings.plugin

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_label_plugin
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PluginScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_label_plugin)) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBackClick = onBackClick,
        modifier = modifier,
    ) {
        Setting(
            title = { Text(text = "PDFプラグイン") },
            onClick = {},
            widget = {
                IconButton(onClick = {
                    // onClick
                }) {
                    Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                }
            }
        )
    }
}

@Preview
@Composable
private fun PreviewPluginScreen() {
    PreviewTheme {
        PluginScreen(
            onBackClick = {},
        )
    }
}
