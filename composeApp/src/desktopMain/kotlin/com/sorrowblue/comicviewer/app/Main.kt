package com.sorrowblue.comicviewer.app

import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import comicviewer.composeapp.generated.resources.Res
import comicviewer.composeapp.generated.resources.app_label_exit
import java.awt.Dimension
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    val trayState = rememberTrayState()
    val notification = rememberNotification("Notification", "Message from MyApp!")
    Tray(
        state = trayState,
        icon = rememberVectorPainter(ComicIcons.Launcher),
        onAction = {
            trayState.sendNotification(notification)
        },
        menu = {
            Item(stringResource(Res.string.app_label_exit), onClick = ::exitApplication)
        },
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComicViewer",
        icon = rememberVectorPainter(ComicIcons.Launcher),
    ) {
        trayState.sendNotification(notification)
        window.minimumSize = Dimension(400, 600)
        Application(finishApp = ::exitApplication)
    }
}
