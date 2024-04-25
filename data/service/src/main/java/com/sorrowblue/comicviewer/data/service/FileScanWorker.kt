package com.sorrowblue.comicviewer.data.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.domain.model.SortUtil
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import com.sorrowblue.comicviewer.framework.notification.R as NotificationR

@HiltWorker
internal class FileScanWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val factory: RemoteDataSource.Factory,
    private val fileLocalDataSource: FileLocalDataSource,
) : CoroutineWorker(appContext, workerParams) {

    private val notificationManager = NotificationManagerCompat.from(appContext)
    private val notificationID = Random.nextInt()

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("", "")
    }

    override suspend fun doWork(): Result {
        val request = FileScanRequest.fromWorkData(inputData) ?: return Result.failure()
        val bookshelf = bookshelfLocalDataSource.flow(request.bookshelfId).first()
            ?: return Result.failure()
        setForeground(createForegroundInfo(bookshelf.displayName, "", true))
        return runCatching {
            scan(request, bookshelf)
        }.onFailure {
            val notification =
                NotificationCompat.Builder(applicationContext, ChannelID.SCAN_BOOKSHELF.id)
                    .setContentTitle("本棚のスキャン")
                    .setContentText("スキャンはキャンセルされました。")
                    .setSubText(bookshelf.displayName)
                    .setSmallIcon(NotificationR.drawable.ic_sync_disabled_24)
                    .setOngoing(false)
                    .build()
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(notificationID, notification)
            }
        }.getOrElse { Result.failure() }
    }

    private suspend fun scan(request: FileScanRequest, bookshelf: Bookshelf): Result {
        val remoteDataSource = factory.create(bookshelf)
        val rootFolder =
            fileLocalDataSource.root(request.bookshelfId) ?: return Result.failure()
        remoteDataSource.nestedListFiles(bookshelf, rootFolder, request)

        val notification =
            NotificationCompat.Builder(applicationContext, ChannelID.SCAN_BOOKSHELF.id)
                .setContentTitle("本棚のスキャンが完了しました")
                .setSubText(bookshelf.displayName)
                .setSmallIcon(NotificationR.drawable.ic_sync_disabled_24)
                .setOngoing(false)
                .build()
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(Random.nextInt(), notification)
        }
        return Result.success()
    }

    private suspend fun RemoteDataSource.nestedListFiles(
        bookshelf: Bookshelf,
        file: File,
        request: FileScanRequest,
    ) {
        delay(2000)
        setProgress(workDataOf("path" to file.path))
        setForeground(createForegroundInfo(bookshelf.displayName, file.path))

        val fileModelList = SortUtil.sortedIndex(
            listFiles(file, request.resolveImageFolder) {
                SortUtil.filter(it, request.supportExtensions)
            }
        )
        fileLocalDataSource.updateHistory(file, fileModelList)
        fileModelList.filterIsInstance<IFolder>()
            .forEach { nestedListFiles(bookshelf, it, request) }
    }

    private fun createForegroundInfo(
        bookshelfName: String,
        path: String,
        init: Boolean = false,
    ): ForegroundInfo {
        val cancelIntent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)
        val notification =
            NotificationCompat.Builder(applicationContext, ChannelID.SCAN_BOOKSHELF.id).apply {
                setContentTitle(applicationContext.getString(R.string.scan_bookshelf))
                setSubText(bookshelfName)
                setContentText(path)
                setSmallIcon(NotificationR.drawable.ic_sync_24)
                addAction(
                    R.drawable.ic_download_24,
                    applicationContext.getString(android.R.string.cancel),
                    cancelIntent
                )
                // 消去不可
                setOngoing(true)
                // サイレント通知
                setSilent(!init)
                // 即時表示
                setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            }.build()

        return ForegroundInfo(
            notificationID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }
}
