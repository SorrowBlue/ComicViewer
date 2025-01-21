package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.viewmodel.koinViewModel

@Stable
internal interface ViewerSettingsScreenState {
    val uiState: SettingsViewerScreenUiState
    fun onStatusBarShowChange(value: Boolean)
    fun onNavigationBarShowChange(value: Boolean)
    fun onTurnOnScreenChange(value: Boolean)
    fun onCutWhitespaceChange(value: Boolean)
    fun onCacheImageChange(value: Boolean)
    fun onDisplayFirstPageChange(value: Boolean)
    fun onImageQualityChange(value: Float)
    fun onPreloadPagesChange(value: Float)
    fun onFixScreenBrightnessChange(value: Boolean)
    fun onScreenBrightnessChange(value: Float)
}

@Composable
internal fun rememberViewerSettingsScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: ViewerSettingsViewModel = koinViewModel(),
): ViewerSettingsScreenState = remember {
    ViewerSettingsScreenStateImpl(scope = scope, viewModel = viewModel)
}

private class ViewerSettingsScreenStateImpl(
    scope: CoroutineScope,
    private val viewModel: ViewerSettingsViewModel,
) : ViewerSettingsScreenState {

    override var uiState: SettingsViewerScreenUiState by mutableStateOf(SettingsViewerScreenUiState())
        private set

    init {
        viewModel.settings.onEach {
            uiState = uiState.copy(
                isStatusBarShow = it.showStatusBar,
                isNavigationBarShow = it.showNavigationBar,
                isTurnOnScreen = it.keepOnScreen,
                isCacheImage = false,
                isDisplayFirstPage = false,
                isCutWhitespace = false,
                preloadPages = it.readAheadPageCount.toFloat(),
                imageQuality = it.imageQuality.toFloat(),
                isFixScreenBrightness = it.enableBrightnessControl,
                screenBrightness = it.screenBrightness
            )
        }.launchIn(scope)
    }

    override fun onStatusBarShowChange(value: Boolean) {
        viewModel.onStatusBarShowChange(value)
    }

    override fun onNavigationBarShowChange(value: Boolean) {
        viewModel.onNavigationBarShowChange(value)
    }

    override fun onTurnOnScreenChange(value: Boolean) {
        viewModel.onTurnOnScreenChange(value)
    }

    override fun onCutWhitespaceChange(value: Boolean) {
        viewModel.onCutWhitespaceChange(value)
    }

    override fun onCacheImageChange(value: Boolean) {
        viewModel.onCacheImageChange(value)
    }

    override fun onDisplayFirstPageChange(value: Boolean) {
        viewModel.onDisplayFirstPageChange(value)
    }

    override fun onImageQualityChange(value: Float) {
        viewModel.onImageQualityChange(value)
    }

    override fun onPreloadPagesChange(value: Float) {
        viewModel.onPreloadPagesChange(value)
    }

    override fun onFixScreenBrightnessChange(value: Boolean) {
        viewModel.onFixScreenBrightnessChange(value)
    }

    override fun onScreenBrightnessChange(value: Float) {
        viewModel.onScreenBrightnessChange(value)
    }
}
