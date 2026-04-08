package com.sorrowblue.comicviewer.feature.bookshelf.info.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.framework.background.MetroWorkerInstanceFactory
import com.sorrowblue.comicviewer.framework.notification.AndroidNotificationChannel
import com.sorrowblue.comicviewer.framework.notification.R
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_description_thumbnail_scan_cancelled
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_title_thumbnail_scan
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_title_thumbnail_scan_completed
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import logcat.asLog
import logcat.logcat
import org.jetbrains.compose.resources.getString

@AssistedInject
internal class ThumbnailScanWorker(
    appContext: Context,
    @Assisted params: WorkerParameters,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val regenerateThumbnailsUseCase: RegenerateThumbnailsUseCase,
    private val notificationManager: NotificationManagerCompat,
) : CoroutineWorker(appContext, params) {
    @AssistedFactory
    fun interface Factory : MetroWorkerInstanceFactory<ThumbnailScanWorker>

    private val notificationID = Random.nextInt()

    override suspend fun getForegroundInfo(): ForegroundInfo = createForegroundInfo("", 0, 0, true)

    override suspend fun doWork(): Result {
        @OptIn(InternalDataApi::class)
        val bookshelfId = BookshelfId(inputData.getInt(BOOKSHELF_ID, 0))
        val bookshelfInfo =
            getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId))
                .first()
                .dataOrNull() ?: return Result.failure()
        setForeground(createForegroundInfo(bookshelfInfo.bookshelf.displayName, 0, 0, true))
        return try {
            innerWork(bookshelfInfo)
        } catch (e: kotlinx.coroutines.CancellationException) {
            logcat { "catch: ${e.asLog()}" }
            val notification =
                NotificationCompat
                    .Builder(applicationContext, AndroidNotificationChannel.SCAN_BOOKSHELF.id)
                    .setContentTitle(
                        getString(Res.string.bookshelf_info_notification_title_thumbnail_scan),
                    )
                    .setContentText(
                        getString(
                            Res.string.bookshelf_info_notification_description_thumbnail_scan_cancelled,
                        ),
                    )
                    .setSubText(bookshelfInfo.bookshelf.displayName)
                    .setSmallIcon(R.drawable.ic_sync_cancel_24dp)
                    .setOngoing(false)
                    .build()
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(notificationID, notification)
            }
            Result.failure()
        }
    }

    private suspend fun innerWork(bookshelfInfo: BookshelfFolder): Result {
        val useCaseRequest =
            RegenerateThumbnailsUseCase.Request(
                bookshelfInfo.bookshelf.id,
            ) { bookshelf, progress, max ->
                setForeground(createForegroundInfo(bookshelf.displayName, progress, max))
            }
        return regenerateThumbnailsUseCase(useCaseRequest).fold({
            val notification =
                NotificationCompat
                    .Builder(applicationContext, AndroidNotificationChannel.SCAN_BOOKSHELF.id)
                    .setContentTitle(
                        getString(
                            Res.string.bookshelf_info_notification_title_thumbnail_scan_completed,
                        ),
                    )
                    .setSubText(bookshelfInfo.bookshelf.displayName)
                    .setSmallIcon(R.drawable.ic_sync_done_24dp)
                    .setOngoing(false)
                    .build()
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(Random.nextInt(), notification)
            }
            delay(5000)
            Result.success()
        }, {
            Result.failure()
        })
    }

    private suspend fun createForegroundInfo(
        bookshelfName: String,
        progress: Long,
        max: Long,
        init: Boolean = false,
    ): ForegroundInfo {
        val cancelIntent = WorkManager
            .getInstance(applicationContext)
            .createCancelPendingIntent(id)
        val notification =
            NotificationCompat
                .Builder(applicationContext, AndroidNotificationChannel.SCAN_BOOKSHELF.id)
                .apply {
                    setContentTitle(
                        getString(Res.string.bookshelf_info_notification_title_thumbnail_scan),
                    )
                    setSubText(bookshelfName)
                    setContentText("$progress/$max")
                    setProgress(max.toInt(), progress.toInt(), init)
                    setSmallIcon(R.drawable.ic_sync_image_24dp)
                    addAction(
                        R.drawable.ic_sync_cancel_24dp,
                        applicationContext.getString(android.R.string.cancel),
                        cancelIntent,
                    )
                    // 消去不可
                    setOngoing(true)
                    // サイレント通知
                    setSilent(!init)
                    // 即時表示
                    foregroundServiceBehavior = NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
                }.build()

        return ForegroundInfo(
            notificationID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
        )
    }

    companion object {
        private const val BOOKSHELF_ID = "BOOKSHELF_ID"
        private const val UNIQUE_WORK_NAME_PREFIX =
            "com.sorrowblue.comicviewer.feature.bookshelf.info.worker.ThumbnailScanWorker:"

        private fun uniqueWorkName(id: BookshelfId) = "$UNIQUE_WORK_NAME_PREFIX$id"

        fun getWorkInfosFlow(workManager: WorkManager, id: BookshelfId): Flow<List<WorkInfo>> =
            workManager.getWorkInfosForUniqueWorkFlow(uniqueWorkName(id))

        fun enqueueUniqueWork(context: Context, bookshelfId: BookshelfId): Operation {
            val constraints = Constraints
                .Builder()
                .apply {
                    // 有効なネットワーク接続が必要
                    setRequiredNetworkType(NetworkType.CONNECTED)
                    // ユーザーのデバイスの保存容量が少なすぎる場合以外
                    setRequiresStorageNotLow(true)
                }.build()
            val myWorkRequest = OneTimeWorkRequest
                .Builder(ThumbnailScanWorker::class.java)
                .setConstraints(constraints)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(workDataOf(BOOKSHELF_ID to bookshelfId.value))
                .build()
            return WorkManager
                .getInstance(context)
                .enqueueUniqueWork(
                    uniqueWorkName(bookshelfId),
                    ExistingWorkPolicy.KEEP,
                    myWorkRequest,
                )
        }
    }
}
