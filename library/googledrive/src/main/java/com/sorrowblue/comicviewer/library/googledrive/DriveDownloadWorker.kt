package com.sorrowblue.comicviewer.library.googledrive

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.notification.createNotification
import com.sorrowblue.comicviewer.framework.resource.R
import kotlin.math.ceil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import logcat.logcat


internal class DriveDownloadWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        private const val NOTIFICATION_ID: Int = 2
    }

    private val notificationManager = NotificationManagerCompat.from(applicationContext)

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification(
            applicationContext,
            ChannelID.DOWNLOAD,
            R.drawable.ic_twotone_downloading_24
        ) {
            setContentTitle("バックグラウンドで実行")
        })
    }

    override suspend fun doWork(): Result {
        val outputUri = inputData.getString("outputUri")?.toUri()
            ?: return Result.failure(workDataOf("message" to "出力ファイルがない"))
        val fileId = inputData.getString("fileId")
            ?: return Result.failure(workDataOf("message" to "fileIdがない"))
        val signInAccount = GoogleSignIn.getLastSignedInAccount(applicationContext)
            ?: return Result.failure(workDataOf("message" to "アカウント情報が無効"))
        val credential =
            GoogleAccountCredential.usingOAuth2(
                applicationContext,
                listOf(DriveScopes.DRIVE_READONLY)
            )
        setForeground(getForegroundInfo())
        credential.selectedAccount = signInAccount.account
        val driverService =
            Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                .setApplicationName("ComicViewer")
                .build()

        return withContext(Dispatchers.IO) {
            val request = driverService.files().get(fileId)
            val name = request.execute().name
            request.mediaHttpDownloader.isDirectDownloadEnabled = false
            request.mediaHttpDownloader.chunkSize = MediaHttpDownloader.MAXIMUM_CHUNK_SIZE
            request.mediaHttpDownloader.setProgressListener {
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
            applicationContext.contentResolver.openOutputStream(outputUri).use {
                delay(2000)
                request.executeMediaAndDownloadTo(it)
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
            notificationManager.notify(tag,NOTIFICATION_ID,
                createNotification(
                    applicationContext,
                    ChannelID.DOWNLOAD,
                    R.drawable.ic_twotone_downloading_24
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
            notificationManager.notify(tag, NOTIFICATION_ID,
                createNotification(
                    applicationContext,
                    ChannelID.DOWNLOAD,
                    R.drawable.ic_twotone_downloading_24
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
            notificationManager.notify(tag, NOTIFICATION_ID,
                createNotification(
                    applicationContext,
                    ChannelID.DOWNLOAD,
                    R.drawable.ic_twotone_downloading_24
                ) {
                    setContentTitle("1個のファイルをダウンロードしました。")
                    setContentText(name)
                    setProgress(0, 0, false)
                }
            )
        }
    }
}
