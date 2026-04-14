package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Stable
internal interface ViewerSettingsScreenState {
    val uiState: SettingsViewerScreenUiState

    fun onStatusBarShowChange(value: Boolean)

    fun onNavigationBarShowChange(value: Boolean)

    fun onTurnOnScreenChange(value: Boolean)

    fun onCutWhitespaceChange(value: Boolean)

    fun onDisplayFirstPageChange(value: Boolean)

    fun onBindingDirectionScreenResult(value: BindingDirection)

    fun onImageQualityChange(value: Float)

    fun onPreloadPagesChange(value: Float)

    fun onFixScreenBrightnessChange(value: Boolean)

    fun onScreenBrightnessChange(value: Float)
}

@Composable
context(context: ViewerSettingsScreenContext)
internal fun rememberViewerSettingsScreenState(): ViewerSettingsScreenState {
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    return remember(coroutineScope) {
        ViewerSettingsScreenStateImpl(
            coroutineScope = coroutineScope,
            systemUiController = systemUiController,
            manageViewerSettingsUseCase = context.manageViewerSettingsUseCase,
        )
    }
}

private class ViewerSettingsScreenStateImpl(
    private val coroutineScope: CoroutineScope,
    private val systemUiController: SystemUiController,
    private val manageViewerSettingsUseCase: ManageViewerSettingsUseCase,
) : ViewerSettingsScreenState {
    override var uiState: SettingsViewerScreenUiState by mutableStateOf(
        SettingsViewerScreenUiState(),
    )
        private set

    init {
        manageViewerSettingsUseCase.settings
            .onEach {
                uiState = uiState.copy(
                    isStatusBarShow = it.showStatusBar,
                    isNavigationBarShow = it.showNavigationBar,
                    isTurnOnScreen = it.keepOnScreen,
                    isDisplayFirstPage = it.alwaysOpenFromFirstPage,
                    isCutWhitespace = it.cutWhitespace,
                    preloadPages = it.readAheadPageCount.toFloat(),
                    imageQuality = it.imageQuality.toFloat(),
                    imageFormat = it.imageFormat,
                    isFixScreenBrightness = it.enableBrightnessControl,
                    screenBrightness = it.screenBrightness,
                    bindingDirection = it.bindingDirection,
                )
            }.launchIn(coroutineScope)
    }

    override fun onStatusBarShowChange(value: Boolean) {
        coroutineScope.launch {
            manageViewerSettingsUseCase.edit { it.copy(showStatusBar = value) }
        }
    }

    override fun onNavigationBarShowChange(value: Boolean) {
        coroutineScope.launch {
            manageViewerSettingsUseCase.edit { it.copy(showNavigationBar = value) }
        }
    }

    override fun onTurnOnScreenChange(value: Boolean) {
        coroutineScope.launch {
            manageViewerSettingsUseCase.edit { it.copy(keepOnScreen = value) }
        }
    }

    override fun onCutWhitespaceChange(value: Boolean) {
        coroutineScope.launch {
            manageViewerSettingsUseCase.edit { it.copy(cutWhitespace = value) }
        }
    }

    override fun onBindingDirectionScreenResult(value: BindingDirection) {
        coroutineScope.launch {
            manageViewerSettingsUseCase.edit { it.copy(bindingDirection = value) }
        }
    }

    override fun onDisplayFirstPageChange(value: Boolean) {
        coroutineScope.launch {
            manageViewerSettingsUseCase.edit { it.copy(alwaysOpenFromFirstPage = value) }
        }
    }

    override fun onImageQualityChange(value: Float) {
        coroutineScope.launch {
            manageViewerSettingsUseCase.edit { it.copy(imageQuality = value.toInt()) }
        }
    }

    override fun onPreloadPagesChange(value: Float) {
        coroutineScope.launch {
            manageViewerSettingsUseCase.edit { it.copy(readAheadPageCount = value.toInt()) }
        }
    }

    override fun onFixScreenBrightnessChange(value: Boolean) {
        coroutineScope.launch {
            manageViewerSettingsUseCase.edit { it.copy(enableBrightnessControl = value) }
        }
    }

    private var job: Job? = null
    override fun onScreenBrightnessChange(value: Float) {
        coroutineScope.launch {
            manageViewerSettingsUseCase.edit { it.copy(screenBrightness = value) }
        }
        job?.cancel()
        job = coroutineScope.launch {
            uiState = uiState.copy(screenBrightnessPreview = true, screenBrightnessPreviewTime = 3)
            systemUiController.screenBrightness = value
            delay(1000)
            uiState = uiState.copy(screenBrightnessPreview = true, screenBrightnessPreviewTime = 2)
            delay(1000)
            uiState = uiState.copy(screenBrightnessPreview = true, screenBrightnessPreviewTime = 1)
            delay(1000)
            uiState = uiState.copy(screenBrightnessPreview = false)
            systemUiController.screenBrightness = SystemUiController.BRIGHTNESS_OVERRIDE_NONE
        }
    }
}
