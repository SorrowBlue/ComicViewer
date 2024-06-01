package com.sorrowblue.comicviewer.bookshelf

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
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random
import kotlinx.coroutines.flow.first
import logcat.asLog
import logcat.logcat
import com.sorrowblue.comicviewer.framework.notification.R as NotificationR

@HiltWorker
internal class FileScanWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val scanBookshelfUseCase: ScanBookshelfUseCase,
) : CoroutineWorker(appContext, workerParams) {

    private val notificationManager = NotificationManagerCompat.from(appContext)
    private val notificationID = Random.nextInt()

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("", "")
    }

    override suspend fun doWork(): Result {
        val request = FileScanRequest.fromWorkData(inputData) ?: return Result.failure()
        val bookshelfInfo =
            getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(request.bookshelfId))
                .first().dataOrNull() ?: return Result.failure()
        setForeground(createForegroundInfo(bookshelfInfo.bookshelf.displayName, "", true))
        val useCaseRequest = ScanBookshelfUseCase.Request(request.bookshelfId) { bookshelf, file ->
            kotlin.runCatching {
                if (request.withThumbnails && file is Book) {
                    val imageRequest = ImageRequest.Builder(applicationContext).data(file).build()
                    SingletonImageLoader.get(applicationContext).enqueue(imageRequest)
                }
            }.onFailure {
                logcat { it.asLog() }
            }
            setProgress(workDataOf("path" to file.path))
            setForeground(createForegroundInfo(bookshelf.displayName, file.path))
        }
        return scanBookshelfUseCase.execute(useCaseRequest).first().fold({
            val notification =
                NotificationCompat.Builder(applicationContext, ChannelID.SCAN_BOOKSHELF.id)
                    .setContentTitle("本棚のスキャンが完了しました")
                    .setSubText(bookshelfInfo.bookshelf.displayName)
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
            Result.success()
        }, {
            val notification =
                NotificationCompat.Builder(applicationContext, ChannelID.SCAN_BOOKSHELF.id)
                    .setContentTitle("本棚のスキャン")
                    .setContentText("スキャンはキャンセルされました。")
                    .setSubText(bookshelfInfo.bookshelf.displayName)
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
            Result.failure()
        })
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
                setContentTitle(applicationContext.getString(R.string.bookshelf_title_bookshelf))
                setSubText(bookshelfName)
                setContentText(path)
                setSmallIcon(NotificationR.drawable.ic_sync_24)
                addAction(
                    com.sorrowblue.comicviewer.framework.designsystem.R.drawable.ic_download_24,
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
