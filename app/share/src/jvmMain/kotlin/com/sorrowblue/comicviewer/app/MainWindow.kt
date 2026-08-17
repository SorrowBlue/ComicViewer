/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.sorrowblue.comicviewer.Application
import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import com.sorrowblue.comicviewer.framework.ui.FrameworkResString
import comicviewer.framework.ui.generated.resources.app_name
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
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
context(appGraph: AppGraph)
fun MainWindow(exitApplication: () -> Unit) {
    val state = rememberMainWindowState()

    @Suppress("LeakLensFlowLifecycleLeak")
    val initialSettings by state.windowSettings.collectAsState()
    val settings = initialSettings ?: return
    val windowState = rememberWindowState2(settings)
    Window(
        onCloseRequest = {
            state.saveWindowSettings(
                windowState = windowState,
                settings = settings,
                onComplete = exitApplication,
            )
        },
        state = windowState,
        title = stringResource(FrameworkResString.app_name),
        icon = rememberVectorPainter(ComicIcons.Launcher),
    ) {
        context(appGraph.context) {
            MetroContent {
                val viewModel =
                    assistedMetroViewModel<ComicViewerAppViewModel, ComicViewerAppViewModel.Factory> { create() }
                Application(finishApp = exitApplication)
                SplashScreen(keepOnScreenCondition = { viewModel.shouldKeepSplash.value })
            }
        }
    }
}

private fun isWindowPositionValid(x: Int, y: Int, width: Int, height: Int): Boolean {
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val screens = ge.screenDevices
    val windowRect = Rectangle(x, y, width, height)
    return screens.any { screen ->
        screen.defaultConfiguration.bounds.intersects(windowRect)
    }
}
