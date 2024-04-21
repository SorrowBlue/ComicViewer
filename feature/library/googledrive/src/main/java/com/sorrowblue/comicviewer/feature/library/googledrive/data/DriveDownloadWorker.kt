package com.sorrowblue.comicviewer.feature.library.googledrive.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.sorrowblue.comicviewer.app.IoDispatcher
import com.sorrowblue.comicviewer.feature.library.googledrive.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.FrameworkDrawable
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.notification.createNotification
import kotlin.math.ceil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import logcat.logcat
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

internal class DriveDownloadWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params), KoinComponent {

    companion object {
        private const val NOTIFICATION_ID: Int = 2
    }

    private val dispatcher by inject<CoroutineDispatcher>(qualifier = named<IoDispatcher>())
    private val repository by inject<GoogleDriveApiRepository>()

    private val notificationManager = NotificationManagerCompat.from(applicationContext)

    @Suppress("SpecifyForegroundServiceType")
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            NOTIFICATION_ID,
            createNotification(
                applicationContext,
                ChannelID.DOWNLOAD,
                FrameworkDrawable.ic_download_24
            ) {
                setContentTitle(applicationContext.getString(R.string.googledrive_msg_downloading_background))
            },
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    override suspend fun doWork(): Result {
        val outputUri = inputData.getString("outputUri")?.toUri()
            ?: return Result.failure(workDataOf("message" to "出力ファイルがない"))
        val fileId = inputData.getString("fileId")
            ?: return Result.failure(workDataOf("message" to "fileIdがない"))
        return withContext(dispatcher) {
            val name = repository.fileName(fileId).orEmpty()
            applicationContext.contentResolver.openOutputStream(outputUri)?.use { output ->
                delay(2000)
                repository.download(fileId, output) {
                    when (it.downloadState!!) {
                        MediaHttpDownloader.DownloadState.NOT_STARTED -> {
                            setProgressAsync(workDataOf("progress" to 0))
                            createNotification(fileId, name)
                        }

                        MediaHttpDownloader.DownloadState.MEDIA_IN_PROGRESS -> {
                            setProgressAsync(workDataOf("progress" to it.progress))
                            updateNotification(fileId, name, ceil(it.progress * 100).toInt())
                        }

                        MediaHttpDownloader.DownloadState.MEDIA_COMPLETE -> {
                            setProgressAsync(workDataOf("progress" to 1.0))
                            completeNotification(fileId, name)
                        }
                    }
                }
            }
            logcat { "download success." }
            Result.success()
        }
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
                createNotification(
                    applicationContext,
                    ChannelID.DOWNLOAD,
                    FrameworkDrawable.ic_download_24
                ) {
                    setContentTitle(name)
                    setProgress(0, 0, true)
                }
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
                createNotification(
                    applicationContext,
                    ChannelID.DOWNLOAD,
                    FrameworkDrawable.ic_downloading_24
                ) {
                    setContentTitle(name)
                    setProgress(100, progress, false)
                }
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
                createNotification(
                    applicationContext,
                    ChannelID.DOWNLOAD,
                    FrameworkDrawable.ic_download_done_24
                ) {
                    setContentTitle(applicationContext.getString(R.string.googledrive_msg_one_file_downloaded))
                    setContentText(name)
                    setProgress(0, 0, false)
                }
            )
        }
    }
}
