/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageWindowSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metrox.viewmodel.metroViewModel
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.runBlocking
import logcat.asLog
import logcat.logcat

@Composable
internal fun RecoverableWindow(
    onCloseRequest: () -> Unit,
    state: RecoverableWindowState = rememberRecoverableWindowState(),
    title: String = "Untitled",
    icon: Painter? = null,
    content: @Composable FrameWindowScope.() -> Unit,
) {
    val windowSettings by state.windowSettingsFlow.collectAsStateWithLifecycle(null)
    val windowState = rememberWindowState2(windowSettings ?: return)
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
    val lifecycleOwner = LocalLifecycleOwner.current
    Window(
        onCloseRequest = {
            state.saveWindowSettings(
                windowState = windowState,
                onComplete = onCloseRequest,
            )
        },
        state = windowState,
        title = title,
        icon = icon,
    ) {
        CompositionLocalProvider(
            LocalViewModelStoreOwner provides viewModelStoreOwner,
            LocalLifecycleOwner provides lifecycleOwner,
        ) {
            content()
        }
    }
}

@Composable
private fun rememberWindowState2(settings: WindowSettings): WindowState {
    val initialPosition = if (settings.x != -1 && settings.y != -1 && isWindowPositionValid(
            settings.x,
            settings.y,
            settings.width,
            settings.height,
        )
    ) {
        WindowPosition(settings.x.dp, settings.y.dp)
    } else {
        WindowPosition.PlatformDefault
    }
    return rememberWindowState(
        position = initialPosition,
        size = DpSize(settings.width.dp, settings.height.dp),
        placement = if (settings.isMaximized) WindowPlacement.Maximized else WindowPlacement.Floating,
    )
}

private fun isWindowPositionValid(x: Int, y: Int, width: Int, height: Int): Boolean {
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val screens = ge.screenDevices
    val windowRect = Rectangle(x, y, width, height)
    return screens.any { screen ->
        screen.defaultConfiguration.bounds.intersects(windowRect)
    }
}

/**
 * State holder for [RecoverableWindow] that manages persisting and restoring [WindowSettings].
 */
interface RecoverableWindowState {

    /**
     * Stream of saved [WindowSettings].
     */
    val windowSettingsFlow: SharedFlow<WindowSettings>

    /**
     * Saves the current [windowState] and invokes [onComplete] when finished.
     */
    fun saveWindowSettings(windowState: WindowState, onComplete: () -> Unit)
}

@Composable
internal fun rememberRecoverableWindowState(
    viewModel: RecoverableWindowViewModel = metroViewModel(),
): RecoverableWindowState = remember(viewModel) {
    RecoverableWindowStateImpl(
        windowSettingsFlow = viewModel.windowSettingsFlow,
        updateWindowSettings = {
            viewModel.updateWindowSettings(it)
        },
    )
}

private class RecoverableWindowStateImpl(
    override val windowSettingsFlow: SharedFlow<WindowSettings>,
    private val updateWindowSettings: suspend (WindowSettings) -> Unit,
) : RecoverableWindowState {
    override fun saveWindowSettings(windowState: WindowState, onComplete: () -> Unit) {
        runBlocking {
            runCatching {
                val currentSettings = windowSettingsFlow.replayCache.first()
                val settings = if (windowState.placement == WindowPlacement.Maximized) {
                    currentSettings.copy(isMaximized = true)
                } else {
                    val position = windowState.position
                    val x =
                        if (position is WindowPosition.Absolute) position.x.value.toInt() else currentSettings.x
                    val y =
                        if (position is WindowPosition.Absolute) position.y.value.toInt() else currentSettings.y
                    currentSettings.copy(
                        width = windowState.size.width.value.toInt(),
                        height = windowState.size.height.value.toInt(),
                        x = x,
                        y = y,
                        isMaximized = false,
                    )
                }
                updateWindowSettings(settings)
            }.onFailure { e ->
                logcat { "Failed to save window settings: ${e.asLog()}" }
            }
            onComplete()
        }
    }
}

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class RecoverableWindowViewModel(
    private val settingsUseCase: ManageWindowSettingsUseCase,
) : ViewModel() {

    val windowSettingsFlow = settingsUseCase.settings.shareIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        replay = 1,
    )

    suspend fun updateWindowSettings(windowSettings: WindowSettings) {
        settingsUseCase.edit { windowSettings }
    }
}
