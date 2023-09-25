package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.feature.settings.viewer.component.SettingsViewerTopAppBar
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.CustomSlider
import com.sorrowblue.comicviewer.framework.ui.material3.ListItem2
import com.sorrowblue.comicviewer.framework.ui.material3.ListItemSwitch

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
internal fun SettingsViewerRoute(
    onBackClick: () -> Unit,
    viewModel: SettingsViewerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    SettingsViewerScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onStatusBarShowChange = viewModel::onStatusBarShowChange,
        onNavigationBarShowChange = viewModel::onNavigationBarShowChange,
        onTurnOnScreenChange = viewModel::onTurnOnScreenChange,
        onCutWhitespaceChange = viewModel::onCutWhitespaceChange,
        onCacheImageChange = viewModel::onCacheImageChange,
        onDisplayFirstPageChange = viewModel::onDisplayFirstPageChange,
        onPreloadPagesChange = viewModel::onPreloadPagesChange,
        onImageQualityChange = viewModel::onImageQualityChange,
        onFixScreenBrightnessChange = viewModel::onFixScreenBrightnessChange,
        onScreenBrightnessChange = viewModel::onScreenBrightnessChange,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsViewerScreen(
    uiState: SettingsViewerScreenUiState = SettingsViewerScreenUiState(),
    onBackClick: () -> Unit = {},
    onStatusBarShowChange: (Boolean) -> Unit = {},
    onNavigationBarShowChange: (Boolean) -> Unit = {},
    onTurnOnScreenChange: (Boolean) -> Unit = {},
    onCutWhitespaceChange: (Boolean) -> Unit = {},
    onCacheImageChange: (Boolean) -> Unit = {},
    onDisplayFirstPageChange: (Boolean) -> Unit = {},
    onPreloadPagesChange: (Float) -> Unit = {},
    onImageQualityChange: (Float) -> Unit = {},
    onFixScreenBrightnessChange: (Boolean) -> Unit = {},
    onScreenBrightnessChange: (Float) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            SettingsViewerTopAppBar(
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
        ) {
            ListItemSwitch(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_viewer_title_show_status_bar))
                },
                checked = uiState.isStatusBarShow,
                onCheckedChange = onStatusBarShowChange
            )
            ListItemSwitch(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_viewer_title_show_navigation_bar))
                },
                checked = uiState.isNavigationBarShow,
                onCheckedChange = onNavigationBarShowChange
            )
            ListItemSwitch(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_viewer_title_not_turn_off_screen))
                },
                checked = uiState.isTurnOnScreen,
                onCheckedChange = onTurnOnScreenChange
            )
            ListItemSwitch(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_viewer_title_cut_whitespace))
                },
                checked = uiState.isCutWhitespace,
                onCheckedChange = onCutWhitespaceChange
            )
            ListItemSwitch(
                headlineContent = {
                    Text(text = "画像をキャッシュする")
                },
                checked = uiState.isCacheImage,
                onCheckedChange = onCacheImageChange
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_viewer_title_binding_direction))
                },
            )
            ListItem(
                headlineContent = {
                    Text(text = "各々の本で異なる閉じ方向を設定する")
                },
            )
            ListItemSwitch(
                headlineContent = {
                    Text(text = "常に最初のページを表示する")
                },
                checked = uiState.isDisplayFirstPage,
                onCheckedChange = onDisplayFirstPageChange
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_viewer_label_preload_pages))
                },
                supportingContent = {
                    CustomSlider(
                        value = uiState.preloadPages,
                        onValueChange = onPreloadPagesChange,
                        valueRange = 1f..10f,
                        steps = 8,
                        thumbLabel = {
                            it.toInt().toString()
                        }
                    )
                }
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_viewer_title_image_quality))
                },
                supportingContent = {
                    CustomSlider(
                        value = uiState.imageQuality,
                        onValueChange = onImageQualityChange,
                        valueRange = 1f..100f,
                        steps = 98,
                        thumbLabel = {
                            it.toInt().toString()
                        }
                    )
                }
            )
            Text(
                stringResource(id = R.string.settings_viewer_title_brightness_control),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            ListItemSwitch(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_viewer_title_brightness_control))
                },
                checked = uiState.isFixScreenBrightness,
                onCheckedChange = onFixScreenBrightnessChange
            )
            ListItem2(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_viewer_title_brightness_level))
                },
                enabled = uiState.isFixScreenBrightness,
                supportingContent = {
                    CustomSlider(
                        enabled = uiState.isFixScreenBrightness,
                        value = uiState.screenBrightness,
                        onValueChange = onScreenBrightnessChange,
                        valueRange = 1f..100f,
                        steps = 98,
                        thumbLabel = { it.toInt().toString() }
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSettingsViewerScreen() {
    ComicTheme {
        Surface {
            SettingsViewerScreen()
        }
    }
}
