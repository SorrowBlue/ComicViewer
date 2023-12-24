package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.feature.settings.display.section.AppearanceDialogController
import com.sorrowblue.comicviewer.feature.settings.display.section.rememberAppearanceDialogController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal interface DisplaySettingsScreenState {

    val appearanceDialogController: AppearanceDialogController
    val uiState: SettingsDisplayScreenUiState

    fun onDarkModeChange(darkMode: DarkMode)
    fun onRestoreOnLaunchChange(value: Boolean)
    fun onDarkModeClick()
    fun onAppearanceDismissRequest()
}

@Composable
internal fun rememberDisplaySettingsScreenState(
    appearanceDialogController: AppearanceDialogController = rememberAppearanceDialogController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: SettingsDisplayViewModel = hiltViewModel(),
): DisplaySettingsScreenState = rememberSaveable(
    saver = Saver(
        save = { it.uiState },
        restore = { uiState ->
            DisplaySettingsScreenStateImpl(
                initUiState = uiState,
                appearanceDialogController = appearanceDialogController,
                scope = scope,
                viewModel = viewModel
            )
        }
    )
) {
    DisplaySettingsScreenStateImpl(
        appearanceDialogController = appearanceDialogController,
        scope = scope,
        viewModel = viewModel
    )
}

@Stable
private class DisplaySettingsScreenStateImpl(
    initUiState: SettingsDisplayScreenUiState = SettingsDisplayScreenUiState(),
    override val appearanceDialogController: AppearanceDialogController,
    scope: CoroutineScope,
    private val viewModel: SettingsDisplayViewModel,
) : DisplaySettingsScreenState {

    override var uiState by mutableStateOf(initUiState)
        private set

    init {
        scope.launch {
            viewModel.displaySettings.collectLatest {
                uiState = uiState.copy(darkMode = it.darkMode, restoreOnLaunch = it.restoreOnLaunch)
            }
        }
    }

    override fun onDarkModeChange(darkMode: DarkMode) {
        viewModel.updateDarkMode(darkMode)
        appearanceDialogController.dismiss()
    }

    override fun onRestoreOnLaunchChange(value: Boolean) {
        viewModel.onRestoreOnLaunchChange(value)
    }

    override fun onDarkModeClick() {
        appearanceDialogController.show(uiState.darkMode)
    }

    override fun onAppearanceDismissRequest() {
        appearanceDialogController.dismiss()
    }
}
