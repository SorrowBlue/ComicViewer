/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sorrowblue.comicviewer.Application
import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import com.sorrowblue.comicviewer.framework.ui.FrameworkResString
import comicviewer.framework.ui.generated.resources.app_name
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource

@Composable
fun rememberWindowState2(settings: WindowSettings): WindowState {
    val initialPosition = if (settings.x != -1 && settings.y != -1 && isWindowPositionValid(
            settings.x,
            settings.y,
            settings.width,
            settings.height
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

@Composable
context(appGraph: AppGraph, scope: ApplicationScope)
fun MainWindow(exitApplication: () -> Unit) {
    @Suppress("LeakLensFlowLifecycleLeak")
    val initialSettings by remember { appGraph.jvmDatastoreDataSource.windowSettings }.collectAsState(null)
    val settings = initialSettings ?: return
    val windowState = rememberWindowState2(settings)
    val trayState = rememberTrayState()
    var composeWindow by remember { mutableStateOf<ComposeWindow?>(null) }
    scope.Tray(
        state = trayState,
        icon = rememberVectorPainter(ComicIcons.Launcher),
        onAction = {
            windowState.isMinimized = false
            composeWindow?.let { window ->
                if (window.isShowing) {
                    window.toFront()
                    window.requestFocus()
                }
            }
        },
    )
    Window(
        onCloseRequest = {
            runBlocking {
                try {
                    appGraph.jvmDatastoreDataSource.updateWindowSettings { settings ->
                        if (windowState.placement == WindowPlacement.Maximized) {
                            settings.copy(isMaximized = true)
                        } else {
                            val position = windowState.position
                            val x =
                                if (position is WindowPosition.Absolute) position.x.value.toInt() else settings.x
                            val y =
                                if (position is WindowPosition.Absolute) position.y.value.toInt() else settings.y
                            settings.copy(
                                width = windowState.size.width.value.toInt(),
                                height = windowState.size.height.value.toInt(),
                                x = x,
                                y = y,
                                isMaximized = false,
                            )
                        }
                    }
                } catch (e: Exception) {
                    // ignore
                }
            }
            exitApplication()
        },
        state = windowState,
        title = stringResource(FrameworkResString.app_name),
        icon = rememberVectorPainter(ComicIcons.Launcher),
    ) {
        DisposableEffect(Unit) {
            composeWindow = this@Window.window
            onDispose {
                composeWindow = null
            }
        }
        window.minimumSize = Dimension(400, 600)
        CompositionLocalProvider(LocalMetroViewModelFactory provides appGraph.viewModelFactory) {
            val splashViewModel = viewModel { MainViewModel() }
            Application(finishApp = exitApplication)
            SplashScreen(keepOnScreenCondition = { splashViewModel.shouldKeepSplash.value })
        }
    }
}

@Composable
internal fun SplashScreen(keepOnScreenCondition: () -> Boolean) {
}

private fun isWindowPositionValid(x: Int, y: Int, width: Int, height: Int): Boolean {
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val screens = ge.screenDevices
    val windowRect = Rectangle(x, y, width, height)
    return screens.any { screen ->
        screen.defaultConfiguration.bounds.intersects(windowRect)
    }
}
