package com.sorrowblue.comicviewer.framework.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import logcat.LogPriority
import logcat.logcat

internal class NotificationInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val notificationManager = context.getSystemService<NotificationManager>() ?: return
        val (createChannels, deleteChannels) = AndroidNotificationChannel.entries.partition {
            it.enable
        }
        createChannels.forEach {
            val channel =
                NotificationChannel(it.id, context.getString(it.nameRes), it.important).apply {
                    description = context.getString(it.descriptionRes)
                }
            notificationManager.createNotificationChannel(channel)
        }
        deleteChannels.forEach {
            notificationManager.deleteNotificationChannel(it.id)
        }
        logcat(LogPriority.INFO) { "Initialized notification." }
    }

    override fun dependencies() = listOf(LogcatInitializer::class.java)
}
