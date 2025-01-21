package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Stable
internal interface DisplaySettingsScreenState : SaveableScreenState {

    val uiState: SettingsDisplayScreenUiState

    fun onRestoreOnLaunchChange(value: Boolean)
}

@Composable
internal fun rememberDisplaySettingsScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: DisplaySettingsViewModel = koinViewModel(),
): DisplaySettingsScreenState = rememberSaveableScreenState {
    DisplaySettingsScreenStateImpl(
        scope = scope,
        savedStateHandle = it,
        viewModel = viewModel
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
private class DisplaySettingsScreenStateImpl(
    scope: CoroutineScope,
    override val savedStateHandle: SavedStateHandle,
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

    override fun onRestoreOnLaunchChange(value: Boolean) {
        viewModel.onRestoreOnLaunchChange(value)
    }
}
