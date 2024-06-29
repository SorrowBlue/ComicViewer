package com.sorrowblue.comicviewer.framework.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import logcat.LogPriority
import logcat.logcat

@Suppress("unused")
internal class NotificationInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val notificationManager = context.getSystemService<NotificationManager>() ?: return
        val channels = listOf(
            createNotificationChannel(
                context,
                ChannelID.SCAN_BOOKSHELF,
                NotificationManager.IMPORTANCE_LOW,
                R.string.framework_notification_name_bookshelf_scan,
                R.string.framework_notification_description_bookshelf_scan
            ),
            createNotificationChannel(
                context,
                ChannelID.DOWNLOAD,
                NotificationManager.IMPORTANCE_DEFAULT,
                R.string.framework_notification_name_download,
                R.string.framework_notification_description_download
            )
        )
        notificationManager.createNotificationChannels(channels)
        logcat(LogPriority.INFO) { "Initialized notification." }
    }

    override fun dependencies() = listOf(LogcatInitializer::class.java)

    private fun createNotificationChannel(
        context: Context,
        channelID: ChannelID,
        importance: Int,
        nameRes: Int,
        descriptionRes: Int,
    ): NotificationChannel {
        val name = context.getString(nameRes)
        val channel = NotificationChannel(channelID.id, name, importance)
        channel.description = context.getString(descriptionRes)
        channel.importance
        return channel
    }
}

fun createNotification(
    context: Context,
    channelID: ChannelID,
    smallIcon: Int,
    builder: NotificationCompat.Builder.() -> Unit,
): Notification {
    return NotificationCompat.Builder(context, channelID.id)
        .setSmallIcon(smallIcon)
        .apply(builder)
        .build()
}
