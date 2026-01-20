package com.sorrowblue.comicviewer.framework.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat
import org.jetbrains.compose.resources.getString

internal class NotificationInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val notificationManager = context.getSystemService<NotificationManager>() ?: return
        val (createChannels, deleteChannels) = AndroidNotificationChannel.entries.partition {
            it.enable
        }
        runBlocking {
            createChannels.forEach {
                val channel =
                    NotificationChannel(it.id, getString(it.nameRes), it.important).apply {
                        description = getString(it.descriptionRes)
                    }
                notificationManager.createNotificationChannel(channel)
            }
        }
        deleteChannels.forEach {
            notificationManager.deleteNotificationChannel(it.id)
        }
        logcat(LogPriority.INFO) { "Initialized notification." }
    }

    override fun dependencies() = listOf(LogcatInitializer::class.java)
}
