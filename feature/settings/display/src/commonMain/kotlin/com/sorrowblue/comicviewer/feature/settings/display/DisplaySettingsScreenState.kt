/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Stable
internal interface DisplaySettingsScreenState {
    val uiState: SettingsDisplayScreenUiState

    fun onRestoreOnLaunchChange(value: Boolean)
}

@Composable
internal fun rememberDisplaySettingsScreenState(
    viewModel: DisplaySettingsViewModel = metroViewModel(),
): DisplaySettingsScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope) {
        DisplaySettingsScreenStateImpl(
            coroutineScope = coroutineScope,
            settingsFlow = viewModel.settingsFlow,
            updateSettings = viewModel::updateSettings,
        )
    }
}

private class DisplaySettingsScreenStateImpl(
    coroutineScope: CoroutineScope,
    settingsFlow: SharedFlow<DisplaySettings>,
    private val updateSettings: ((DisplaySettings) -> DisplaySettings) -> Unit,
) : DisplaySettingsScreenState {
    override var uiState by mutableStateOf(SettingsDisplayScreenUiState())
        private set

    init {
        settingsFlow.onEach { settings ->
            uiState = uiState.copy(
                darkMode = settings.darkMode,
                restoreOnLaunch = settings.restoreOnLaunch,
            )
        }.launchIn(coroutineScope)
    }

    override fun onRestoreOnLaunchChange(value: Boolean) {
        updateSettings {
            it.copy(restoreOnLaunch = value)
        }
    }
}
