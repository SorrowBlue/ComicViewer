package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Stable
internal interface DisplaySettingsScreenState : SaveableScreenState {

    val uiState: SettingsDisplayScreenUiState

    fun onRestoreOnLaunchChange(value: Boolean)
}

@Composable
internal fun rememberDisplaySettingsScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    displaySettingsUseCase: ManageDisplaySettingsUseCase = koinInject(),
): DisplaySettingsScreenState = rememberSaveableScreenState {
    DisplaySettingsScreenStateImpl(
        scope = scope,
        savedStateHandle = it,
        displaySettingsUseCase = displaySettingsUseCase
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
private class DisplaySettingsScreenStateImpl(
    private val scope: CoroutineScope,
    private val displaySettingsUseCase: ManageDisplaySettingsUseCase,
    override val savedStateHandle: SavedStateHandle,

    ) : DisplaySettingsScreenState {

    override var uiState by savedStateHandle.saveable { mutableStateOf(SettingsDisplayScreenUiState()) }
        private set

    init {
        scope.launch {
            displaySettingsUseCase.settings.collectLatest {
                uiState = uiState.copy(darkMode = it.darkMode, restoreOnLaunch = it.restoreOnLaunch)
            }
        }
    }

    override fun onRestoreOnLaunchChange(value: Boolean) {
        scope.launch {
            displaySettingsUseCase.edit {
                it.copy(restoreOnLaunch = value)
            }
        }
    }
}
