package com.sorrowblue.comicviewer.framework.notification

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.awt.SystemTray
import java.awt.TrayIcon

@SingleIn(AppScope::class)
@Inject
class DesktopNotification {
    fun notify(title: String, description: String) {
        SystemTray.getSystemTray().trayIcons.firstOrNull()
            ?.displayMessage(title, description, TrayIcon.MessageType.INFO)
    }
}
