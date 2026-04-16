package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.common.SliderSetting
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import com.sorrowblue.comicviewer.feature.settings.viewer.subscreen.readingdirection.displayName
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.viewer.generated.resources.Res
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_brightness_level
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_image_quality
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_keep_screen_on
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_override_brightness
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_preload_page
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_reading_direction
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_show_navigation_bar
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_show_status_bar
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_start_first_page
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_trim_margin
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ViewerSettingsScreen(
    uiState: SettingsViewerScreenUiState,
    onBackClick: () -> Unit,
    onStatusBarShowChange: (Boolean) -> Unit,
    onNavigationBarShowChange: (Boolean) -> Unit,
    onTurnOnScreenChange: (Boolean) -> Unit,
    onCutWhitespaceChange: (Boolean) -> Unit,
    onBindingDirectionClick: () -> Unit,
    onDisplayFirstPageChange: (Boolean) -> Unit,
    onPreloadPagesChange: (Float) -> Unit,
    onImageQualityChange: (Float) -> Unit,
    onFixScreenBrightnessChange: (Boolean) -> Unit,
    onScreenBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbar = remember { SnackbarHostState() }
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_viewer_title)) },
        onBackClick = onBackClick,
        snackbarHost = {
            SnackbarHost(snackbar)
        },
        modifier = modifier,
    ) {
        SwitchSetting(
            title = {
                Text(stringResource(Res.string.settings_viewer_label_show_status_bar))
            },
            checked = uiState.isStatusBarShow,
            onCheckedChange = onStatusBarShowChange,
        )
        SwitchSetting(
            title = {
                Text(stringResource(Res.string.settings_viewer_label_show_navigation_bar))
            },
            checked = uiState.isNavigationBarShow,
            onCheckedChange = onNavigationBarShowChange,
        )
        SwitchSetting(
            title = {
                Text(stringResource(Res.string.settings_viewer_label_keep_screen_on))
            },
            checked = uiState.isTurnOnScreen,
            onCheckedChange = onTurnOnScreenChange,
        )
        SwitchSetting(
            title = {
                Text(stringResource(Res.string.settings_viewer_label_trim_margin))
            },
            checked = uiState.isCutWhitespace,
            onCheckedChange = onCutWhitespaceChange,
        )
        Setting(
            title = {
                Text(stringResource(Res.string.settings_viewer_label_reading_direction))
            },
            summary = {
                Text(uiState.bindingDirection.displayName)
            },
            onClick = onBindingDirectionClick,
        )
        SwitchSetting(
            title = {
                Text(stringResource(Res.string.settings_viewer_label_start_first_page))
            },
            checked = uiState.isDisplayFirstPage,
            onCheckedChange = onDisplayFirstPageChange,
        )
        SliderSetting(
            title = {
                Text(stringResource(Res.string.settings_viewer_label_preload_page))
            },
            value = uiState.preloadPages,
            onValueChange = onPreloadPagesChange,
            valueRange = 0f..10f,
            steps = 8,
        )
        SliderSetting(
            title = {
                Text(stringResource(Res.string.settings_viewer_label_image_quality))
            },
            value = uiState.imageQuality,
            onValueChange = onImageQualityChange,
            valueRange = 1f..100f,
            steps = 50,
        )
        SettingsCategory(title = {
            Text(stringResource(Res.string.settings_viewer_label_override_brightness))
        }) {
            SwitchSetting(
                title = {
                    Text(stringResource(Res.string.settings_viewer_label_override_brightness))
                },
                checked = uiState.isFixScreenBrightness,
                onCheckedChange = onFixScreenBrightnessChange,
            )
            SliderSetting(
                title = {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(stringResource(Res.string.settings_viewer_label_brightness_level))
                        if (uiState.screenBrightnessPreview) {
                            Spacer(Modifier.size(ComicTheme.dimension.padding))
                            Text(
                                "Reverting in ${uiState.screenBrightnessPreviewTime}s...",
                                style = ComicTheme.typography.labelMedium,
                            )
                        }
                    }
                },
                thumbText = { value ->
                    Text(
                        text = (value * 100).toInt().toString(),
                        modifier = Modifier.widthIn(min = 40.dp),
                        textAlign = TextAlign.Center,
                    )
                },
                value = uiState.screenBrightness,
                onValueChange = onScreenBrightnessChange,
                valueRange = 0f..1f,
                enabled = uiState.isFixScreenBrightness,
            )
        }
    }
}

@Preview
@Preview(locale = "ja")
@Composable
private fun ViewerSettingsScreenPreview() {
    PreviewTheme {
        ViewerSettingsScreen(
            uiState = SettingsViewerScreenUiState(),
            onBackClick = {},
            onStatusBarShowChange = {},
            onNavigationBarShowChange = {},
            onTurnOnScreenChange = {},
            onCutWhitespaceChange = {},
            onBindingDirectionClick = {},
            onDisplayFirstPageChange = {},
            onPreloadPagesChange = {},
            onImageQualityChange = {},
            onFixScreenBrightnessChange = {},
            onScreenBrightnessChange = {},
        )
    }
}
