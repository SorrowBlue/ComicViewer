package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

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
    settingsUseCase: ManageViewerSettingsUseCase = koinInject(),
): ViewerSettingsScreenState = remember {
    ViewerSettingsScreenStateImpl(scope = scope, settingsUseCase = settingsUseCase)
}

private class ViewerSettingsScreenStateImpl(
    private val scope: CoroutineScope,
    private val settingsUseCase: ManageViewerSettingsUseCase,
) : ViewerSettingsScreenState {

    override var uiState: SettingsViewerScreenUiState by mutableStateOf(SettingsViewerScreenUiState())
        private set

    init {
        settingsUseCase.settings.onEach {
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
        scope.launch {
            settingsUseCase.edit { it.copy(showStatusBar = value) }
        }
    }

    override fun onNavigationBarShowChange(value: Boolean) {
        scope.launch {
            settingsUseCase.edit { it.copy(showNavigationBar = value) }
        }
    }

    override fun onTurnOnScreenChange(value: Boolean) {
        scope.launch {
            settingsUseCase.edit { it.copy(keepOnScreen = value) }
        }
    }

    override fun onCutWhitespaceChange(value: Boolean) {
        // TODO()
    }

    override fun onCacheImageChange(value: Boolean) {
        // TODO()
    }

    override fun onDisplayFirstPageChange(value: Boolean) {
        // TODO()
    }

    override fun onImageQualityChange(value: Float) {
        scope.launch {
            settingsUseCase.edit { it.copy(imageQuality = value.toInt()) }
        }
    }

    override fun onPreloadPagesChange(value: Float) {
        scope.launch {
            settingsUseCase.edit { it.copy(readAheadPageCount = value.toInt()) }
        }
    }

    override fun onFixScreenBrightnessChange(value: Boolean) {
        scope.launch {
            settingsUseCase.edit { it.copy(enableBrightnessControl = value) }
        }
    }

    override fun onScreenBrightnessChange(value: Float) {
        scope.launch {
            settingsUseCase.edit { it.copy(screenBrightness = value) }
        }
    }
}
