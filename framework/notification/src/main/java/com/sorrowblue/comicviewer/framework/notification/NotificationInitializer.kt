package com.sorrowblue.comicviewer.framework.notification

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.startup.Initializer

internal class NotificationInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        val name = "スキャン状況"
        val descriptionText = "スキャン情報を表示します。"
        val channel = NotificationChannelCompat.Builder(ChannelID.SCAN.id,
            NotificationManagerCompat.IMPORTANCE_LOW).setName(name).setDescription(descriptionText)
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}

enum class ChannelID(val id: String) {
    SCAN("scan")
}

fun createNotification(context: Context, channelID: ChannelID, smallIcon: Int, builder: NotificationCompat.Builder.() -> Unit): Notification {
    return NotificationCompat.Builder(context, channelID.id)
        .setSmallIcon(smallIcon)
        .apply(builder)
        .build()
}

interface NotificationContent {
    var title: String
}
