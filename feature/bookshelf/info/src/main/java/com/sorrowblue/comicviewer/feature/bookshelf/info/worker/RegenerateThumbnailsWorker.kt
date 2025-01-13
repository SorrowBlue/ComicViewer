package com.sorrowblue.comicviewer.feature.bookshelf.info.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.notification.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random
import kotlinx.coroutines.flow.first
import logcat.asLog
import logcat.logcat

@HiltWorker
internal class RegenerateThumbnailsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val regenerateThumbnailsUseCase: RegenerateThumbnailsUseCase,
) : CoroutineWorker(appContext, workerParams) {

    private val notificationManager = NotificationManagerCompat.from(appContext)
    private val notificationID = Random.nextInt()

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("", 0, 0, true)
    }

    override suspend fun doWork(): Result {
        @OptIn(ExperimentalIdValue::class)
        val bookshelfId = BookshelfId(inputData.getInt(BOOKSHELF_ID, 0))
        val bookshelfInfo =
            getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId))
                .first().dataOrNull() ?: return Result.failure()
        setForeground(createForegroundInfo(bookshelfInfo.bookshelf.displayName, 0, 0, true))
        return try {
            innerWork(bookshelfInfo)
        } catch (e: kotlinx.coroutines.CancellationException) {
            logcat { "catch: ${e.asLog()}" }
            val notification =
                NotificationCompat.Builder(applicationContext, ChannelID.SCAN_BOOKSHELF.id)
                    .setContentTitle("サムネイルのスキャン")
                    .setContentText("サムネイルのスキャンはキャンセルされました。")
                    .setSubText(bookshelfInfo.bookshelf.displayName)
                    .setSmallIcon(R.drawable.ic_sync_cancel_24dp)
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
        }
    }

    private suspend fun innerWork(bookshelfInfo: BookshelfFolder): Result {
        val useCaseRequest =
            RegenerateThumbnailsUseCase.Request(bookshelfInfo.bookshelf.id) { bookshelf, progress, max ->
                setForeground(createForegroundInfo(bookshelf.displayName, progress, max))
            }
        return regenerateThumbnailsUseCase(useCaseRequest).first().fold({
            val notification =
                NotificationCompat.Builder(applicationContext, ChannelID.SCAN_BOOKSHELF.id)
                    .setContentTitle("サムネイルのスキャンが完了しました")
                    .setSubText(bookshelfInfo.bookshelf.displayName)
                    .setSmallIcon(R.drawable.ic_sync_done_24dp)
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
            Result.failure()
        })
    }

    private fun createForegroundInfo(
        bookshelfName: String,
        progress: Long,
        max: Long,
        init: Boolean = false,
    ): ForegroundInfo {
        val cancelIntent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)
        val notification =
            NotificationCompat.Builder(applicationContext, ChannelID.SCAN_BOOKSHELF.id).apply {
                setContentTitle(
                    applicationContext.getString(
                        com.sorrowblue.comicviewer.feature.bookshelf.info.R.string.bookshelf_info_title_scan
                    )
                )
                setSubText(bookshelfName)
                setContentText("$progress/$max")
                setProgress(max.toInt(), progress.toInt(), init)
                setSmallIcon(R.drawable.ic_sync_book_24dp)
                addAction(
                    R.drawable.ic_sync_cancel_24dp,
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

    companion object {
        const val BOOKSHELF_ID = "BOOKSHELF_ID"

        fun enqueueUniqueWork(context: Context, bookshelfId: BookshelfId) {
            val constraints = Constraints.Builder().apply {
                // 有効なネットワーク接続が必要
                setRequiredNetworkType(NetworkType.CONNECTED)
                // ユーザーのデバイスの保存容量が少なすぎる場合以外
                setRequiresStorageNotLow(true)
            }.build()
            val myWorkRequest = OneTimeWorkRequest.Builder(RegenerateThumbnailsWorker::class.java)
                .setConstraints(constraints)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(workDataOf(BOOKSHELF_ID to bookshelfId.value))
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork("scan2", ExistingWorkPolicy.KEEP, myWorkRequest)
        }
    }
}
