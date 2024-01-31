package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.feature.settings.display.section.AppearanceDialogController
import com.sorrowblue.comicviewer.feature.settings.display.section.rememberAppearanceDialogController
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Stable
internal interface DisplaySettingsScreenState : SaveableScreenState {

    val appearanceDialogController: AppearanceDialogController
    val uiState: SettingsDisplayScreenUiState

    fun onDarkModeChange(darkMode: DarkMode)
    fun onRestoreOnLaunchChange(value: Boolean)
    fun onDarkModeClick()
    fun onAppearanceDismissRequest()
}

@Composable
internal fun rememberDisplaySettingsScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    appearanceDialogController: AppearanceDialogController = rememberAppearanceDialogController(),
    viewModel: DisplaySettingsViewModel = hiltViewModel(),
): DisplaySettingsScreenState = rememberSaveableScreenState {
    DisplaySettingsScreenStateImpl(
        scope = scope,
        savedStateHandle = it,
        appearanceDialogController = appearanceDialogController,
        viewModel = viewModel
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
private class DisplaySettingsScreenStateImpl(
    scope: CoroutineScope,
    override val savedStateHandle: SavedStateHandle,
    override val appearanceDialogController: AppearanceDialogController,
    private val viewModel: DisplaySettingsViewModel,
) : DisplaySettingsScreenState {

    override var uiState by savedStateHandle.saveable { mutableStateOf(SettingsDisplayScreenUiState()) }
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
