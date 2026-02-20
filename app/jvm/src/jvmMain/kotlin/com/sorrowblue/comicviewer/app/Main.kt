package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sorrowblue.comicviewer.Application
import com.sorrowblue.comicviewer.framework.common.DesktopContext
import com.sorrowblue.comicviewer.framework.common.getPlatformGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import com.sorrowblue.comicviewer.framework.ui.FrameworkResString
import comicviewer.app.jvm.generated.resources.Res
import comicviewer.app.jvm.generated.resources.app_label_exit
import comicviewer.app.jvm.generated.resources.app_label_show
import comicviewer.framework.ui.generated.resources.app_name
import dev.zacsweers.metro.createGraphFactory
import java.awt.Dimension
import org.jetbrains.compose.resources.stringResource

fun main() {
    val context = DesktopContext.invoke()
    val appGraph =
        createGraphFactory<AppGraph.Factory>().createAppGraph(context, LicenseeHelperImpl())
    getPlatformGraph = { appGraph }
    application {
        val trayState = rememberTrayState()
        var isOpen by remember { mutableStateOf(true) }
        Tray(
            state = trayState,
            icon = rememberVectorPainter(ComicIcons.Launcher),
            onAction = {
                isOpen = true
            },
            menu = {
                Item(stringResource(Res.string.app_label_show), onClick = { isOpen = true })
                Item(stringResource(Res.string.app_label_exit), onClick = ::exitApplication)
            },
        )
        Window(
            visible = isOpen,
            onCloseRequest = { isOpen = false },
            title = stringResource(FrameworkResString.app_name),
            icon = rememberVectorPainter(ComicIcons.Launcher),
        ) {
            window.minimumSize = Dimension(400, 600)
            val viewModel = viewModel { MainViewModel() }
            with(context) {
                with(appGraph) {
                    Application(finishApp = ::exitApplication)
                }
            }
            SplashScreen(keepOnScreenCondition = viewModel.shouldKeepSplash::value)
        }
    }
}
