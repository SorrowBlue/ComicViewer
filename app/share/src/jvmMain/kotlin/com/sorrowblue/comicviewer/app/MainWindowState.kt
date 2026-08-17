/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageWindowSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import logcat.asLog
import logcat.logcat

@Composable
context(appGraph: AppGraph)
internal fun rememberMainWindowState(coroutineScope: CoroutineScope = rememberCoroutineScope()): MainWindowStateImpl {
    return remember(appGraph, coroutineScope) {
        MainWindowStateImpl(
            settingsUseCase = appGraph.settingsUseCase,
            coroutineScope = coroutineScope,
        )
    }
}

internal interface MainWindowState {

    val windowSettings: StateFlow<WindowSettings?>

    fun saveWindowSettings(
        windowState: WindowState,
        settings: WindowSettings,
        onComplete: () -> Unit,
    )
}

internal class MainWindowStateImpl(
    coroutineScope: CoroutineScope,
    private val settingsUseCase: ManageWindowSettingsUseCase,
) : MainWindowState {
    override val windowSettings = settingsUseCase.settings.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    override fun saveWindowSettings(
        windowState: WindowState,
        settings: WindowSettings,
        onComplete: () -> Unit,
    ) {
        runBlocking {
            try {
                settingsUseCase.edit { currentSettings ->
                    if (windowState.placement == WindowPlacement.Maximized) {
                        currentSettings.copy(isMaximized = true)
                    } else {
                        val position = windowState.position
                        val x =
                            if (position is WindowPosition.Absolute) position.x.value.toInt() else settings.x
                        val y =
                            if (position is WindowPosition.Absolute) position.y.value.toInt() else settings.y
                        currentSettings.copy(
                            width = windowState.size.width.value.toInt(),
                            height = windowState.size.height.value.toInt(),
                            x = x,
                            y = y,
                            isMaximized = false,
                        )
                    }
                }
            } catch (e: Exception) {
                logcat { "Failed to save window settings: ${e.asLog()}" }
            } finally {
                onComplete()
            }
        }
    }
}
