package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.common.SliderSetting
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import comicviewer.feature.settings.viewer.generated.resources.Res
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_binding_direction_each
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_cache_images
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_display_first_page
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_label_preload_pages
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title_binding_direction
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title_brightness_control
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title_brightness_level
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title_cut_whitespace
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title_image_quality
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title_not_turn_off_screen
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title_show_navigation_bar
import comicviewer.feature.settings.viewer.generated.resources.settings_viewer_title_show_status_bar
import org.jetbrains.compose.resources.stringResource

internal data class SettingsViewerScreenUiState(
    val isStatusBarShow: Boolean = false,
    val isNavigationBarShow: Boolean = false,
    val isTurnOnScreen: Boolean = false,
    val isCutWhitespace: Boolean = false,
    val isCacheImage: Boolean = false,
    val isDisplayFirstPage: Boolean = false,
    val preloadPages: Float = 1f,
    val imageQuality: Float = 75f,
    val isFixScreenBrightness: Boolean = false,
    val screenBrightness: Float = 0.5f,
)

@Composable
internal fun ViewerSettingsScreen(
    uiState: SettingsViewerScreenUiState,
    onBackClick: () -> Unit,
    onStatusBarShowChange: (Boolean) -> Unit,
    onNavigationBarShowChange: (Boolean) -> Unit,
    onTurnOnScreenChange: (Boolean) -> Unit,
    onCutWhitespaceChange: (Boolean) -> Unit,
    onCacheImageChange: (Boolean) -> Unit,
    onDisplayFirstPageChange: (Boolean) -> Unit,
    onPreloadPagesChange: (Float) -> Unit,
    onImageQualityChange: (Float) -> Unit,
    onFixScreenBrightnessChange: (Boolean) -> Unit,
    onScreenBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_viewer_title)) },
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        SwitchSetting(
            title = Res.string.settings_viewer_title_show_status_bar,
            checked = uiState.isStatusBarShow,
            onCheckedChange = onStatusBarShowChange,
        )
        SwitchSetting(
            title = Res.string.settings_viewer_title_show_navigation_bar,
            checked = uiState.isNavigationBarShow,
            onCheckedChange = onNavigationBarShowChange,
        )
        SwitchSetting(
            title = Res.string.settings_viewer_title_not_turn_off_screen,
            checked = uiState.isTurnOnScreen,
            onCheckedChange = onTurnOnScreenChange,
        )
        SwitchSetting(
            title = Res.string.settings_viewer_title_cut_whitespace,
            checked = uiState.isCutWhitespace,
            onCheckedChange = onCutWhitespaceChange,
        )
        SwitchSetting(
            title = Res.string.settings_viewer_label_cache_images,
            checked = uiState.isCacheImage,
            onCheckedChange = onCacheImageChange,
        )
        Setting(title = Res.string.settings_viewer_title_binding_direction, onClick = {})
        Setting(title = Res.string.settings_viewer_label_binding_direction_each, onClick = {})
        SwitchSetting(
            title = Res.string.settings_viewer_label_display_first_page,
            checked = uiState.isDisplayFirstPage,
            onCheckedChange = onDisplayFirstPageChange,
        )
        SliderSetting(
            title = Res.string.settings_viewer_label_preload_pages,
            value = uiState.preloadPages,
            onValueChange = onPreloadPagesChange,
            valueRange = 1f..10f,
            steps = 8,
        )
        SliderSetting(
            title = Res.string.settings_viewer_title_image_quality,
            value = uiState.imageQuality,
            onValueChange = onImageQualityChange,
            valueRange = 1f..100f,
            steps = 50,
        )
        SettingsCategory(title = Res.string.settings_viewer_title_brightness_control) {
            SwitchSetting(
                title = Res.string.settings_viewer_title_brightness_control,
                checked = uiState.isFixScreenBrightness,
                onCheckedChange = onFixScreenBrightnessChange,
            )
        }
        SliderSetting(
            title = Res.string.settings_viewer_title_brightness_level,
            value = uiState.screenBrightness,
            onValueChange = onScreenBrightnessChange,
            valueRange = 1f..100f,
            steps = 98,
            enabled = uiState.isFixScreenBrightness,
        )
    }
}
