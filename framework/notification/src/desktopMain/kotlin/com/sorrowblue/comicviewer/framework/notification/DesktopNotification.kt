package com.sorrowblue.comicviewer.framework.notification

import comicviewer.framework.notification.generated.resources.Res
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

@SingleIn(AppScope::class)
@Inject
class DesktopNotification {
    private val trayIcons: MutableMap<Int, TrayIcon> = mutableMapOf()

    fun notify(title: String, description: String) {
        val uri = Res.getUri("drawable/ic_sync_image_24dp.xml")
        val image = Toolkit.getDefaultToolkit().getImage(uri)
        val trayIcon = TrayIcon(image).apply {
            isImageAutoSize = true
        }
        SystemTray.getSystemTray().add(trayIcon)
            .also { trayIcons[0] = trayIcon }
        trayIcon.displayMessage(title, description, TrayIcon.MessageType.INFO)
    }
}
