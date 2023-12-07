package com.sorrowblue.comicviewer.feature.library.onedrive

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.app.IoDispatcher
import com.sorrowblue.comicviewer.feature.library.onedrive.data.OneDriveApiRepository
import com.sorrowblue.comicviewer.framework.designsystem.icon.FrameworkDrawable
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import kotlin.math.ceil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import logcat.logcat
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

internal class OneDriveDownloadWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params), KoinComponent {

    companion object {
        private const val NOTIFICATION_ID: Int = 4
    }

    private val repository by inject<OneDriveApiRepository>()

    private val dispatcher by inject<CoroutineDispatcher>(qualifier = named<IoDispatcher>())

    private val notificationManager = NotificationManagerCompat.from(applicationContext)

    private val notificationBuilder = NotificationCompat.Builder(appContext, ChannelID.DOWNLOAD.id)
        .setSmallIcon(FrameworkDrawable.ic_twotone_downloading_24)

    @Suppress("SpecifyForegroundServiceType")
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            NOTIFICATION_ID,
            notificationBuilder.setContentTitle(applicationContext.getString(R.string.onedrive_msg_downloading_background))
                .build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
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
        withContext(dispatcher) {
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
                    .setContentTitle(applicationContext.getString(R.string.onedrive_msg_one_file_downloaded))
                    .setContentText(name)
                    .setProgress(0, 0, false).build()
            )
        }
    }
}
