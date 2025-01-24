package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Stable
internal interface DisplaySettingsScreenState {

    val uiState: SettingsDisplayScreenUiState

    fun onRestoreOnLaunchChange(value: Boolean)
}

@Composable
internal fun rememberDisplaySettingsScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    displaySettingsUseCase: ManageDisplaySettingsUseCase = koinInject(),
): DisplaySettingsScreenState = remember {
    DisplaySettingsScreenStateImpl(
        scope = scope,
        displaySettingsUseCase = displaySettingsUseCase
    )
}

private class DisplaySettingsScreenStateImpl(
    private val scope: CoroutineScope,
    private val displaySettingsUseCase: ManageDisplaySettingsUseCase,
) : DisplaySettingsScreenState {

    override var uiState by mutableStateOf(SettingsDisplayScreenUiState())
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
