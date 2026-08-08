/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sorrowblue.comicviewer.app.MainViewModel
import com.sorrowblue.comicviewer.app.initFileKit
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import com.sorrowblue.comicviewer.framework.ui.FrameworkResString
import comicviewer.framework.ui.generated.resources.app_name
import java.awt.Dimension
import org.jetbrains.compose.resources.stringResource

fun main() {
    initFileKit(appId = "com.sorrowblue.comicviewer")
    val jvmApplication = JvmApplication()
    application {
        val trayState = rememberTrayState()
        val windowState = rememberWindowState()
        var composeWindow by remember { mutableStateOf<ComposeWindow?>(null) }
        Tray(
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
            onCloseRequest = ::exitApplication,
            state = windowState, title = stringResource(FrameworkResString.app_name),
            icon = rememberVectorPainter(ComicIcons.Launcher),
        ) {
            DisposableEffect(Unit) {
                composeWindow = this@Window.window
                onDispose {
                    composeWindow = null
                }
            }
            window.minimumSize = Dimension(400, 600)
            val viewModel = viewModel { MainViewModel() }
            context(jvmApplication, jvmApplication.appGraph) {
                Application(finishApp = ::exitApplication)
            }
            SplashScreen(keepOnScreenCondition = { viewModel.shouldKeepSplash.value })
        }
    }
}
