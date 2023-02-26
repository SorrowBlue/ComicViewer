package com.sorrowblue.comicviewer.library.onedrive.list

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.resource.R
import com.sorrowblue.comicviewer.library.onedrive.data.OneDriveApiRepository
import kotlin.math.ceil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import logcat.logcat

internal class OneDriveDownloadWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val NOTIFICATION_ID: Int = 4
    }

    private val repository = OneDriveApiRepository.getInstance(appContext)

    private val notificationManager = NotificationManagerCompat.from(applicationContext)

    private val notificationBuilder = NotificationCompat.Builder(appContext, ChannelID.DOWNLOAD.id)
        .setSmallIcon(R.drawable.ic_twotone_downloading_24)

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            NOTIFICATION_ID,
            notificationBuilder.setContentTitle("バックグラウンドでダウンロード中").build()
        )
    }

    override suspend fun doWork(): Result {
        val outputUri = inputData.getString("outputUri")?.toUri()
            ?: return Result.failure(workDataOf("message" to "出力ファイルがない"))
        val driveId = inputData.getString("driveId")
            ?: return Result.failure(workDataOf("message" to "driveIdがない"))
        val itemId = inputData.getString("itemId")
            ?: return Result.failure(workDataOf("message" to "driveIdがない"))
        setForeground(getForegroundInfo())
        createNotification(itemId, itemId)
        delay(2000)
        withContext(Dispatchers.IO) {
            applicationContext.contentResolver.openOutputStream(outputUri)!!.use { stream ->
                repository.download(driveId, itemId, stream) {
                    updateNotification(itemId, itemId, ceil(it * 100).toInt())
                }
            }
        }
        logcat { "download success." }
        delay(2000)
        completeNotification(itemId, itemId)
        return Result.success()
    }

    private fun createNotification(tag: String, name: String) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(
                tag,
                NOTIFICATION_ID,
                notificationBuilder
                    .setContentTitle(name)
                    .setProgress(100, 0, true).build()
            )
        }
    }


    private fun updateNotification(tag: String, name: String, progress: Int) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(
                tag,
                NOTIFICATION_ID,
                notificationBuilder
                    .setContentTitle(name)
                    .setContentText(null)
                    .setProgress(100, progress, false).build()
            )
        }
    }

    private fun completeNotification(tag: String, name: String) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(
                tag,
                NOTIFICATION_ID,
                notificationBuilder
                    .setContentTitle("1個のファイルをダウンロードしました。")
                    .setContentText(name)
                    .setProgress(0, 0, false).build()
            )
        }
    }
}
