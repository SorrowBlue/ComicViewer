/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.display.darkmode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal interface DarkModeScreenState {
    val uiState: DarkModeScreenUiState

    fun onDarkModeChange(darkMode: DarkMode, done: () -> Unit)
}

@Composable
internal fun rememberDarkModeScreenState(
    viewModel: DarkModeViewModel = metroViewModel(),
): DarkModeScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return remember(coroutineScope, lifecycle) {
        DarkModeScreenStateImpl(
            coroutineScope = coroutineScope,
            lifecycle = lifecycle,
            settingsFlow = viewModel.settingsFlow,
            updateSettings = viewModel::updateSettings,
        )
    }
}

private class DarkModeScreenStateImpl(
    coroutineScope: CoroutineScope,
    lifecycle: Lifecycle,
    settingsFlow: SharedFlow<DisplaySettings>,
    private val updateSettings: ((DisplaySettings) -> DisplaySettings, () -> Unit) -> Unit,
) : DarkModeScreenState {
    override var uiState by mutableStateOf(DarkModeScreenUiState())

    init {
        settingsFlow.flowWithLifecycle(lifecycle)
            .onEach {
                uiState = uiState.copy(darkMode = it.darkMode)
            }.launchIn(coroutineScope)
    }

    override fun onDarkModeChange(darkMode: DarkMode, done: () -> Unit) {
        updateSettings(
            { it.copy(darkMode = darkMode) },
            {
                updateDarkMode(darkMode)
                done()
            },
        )
    }
}

internal expect fun updateDarkMode(darkMode: DarkMode)
