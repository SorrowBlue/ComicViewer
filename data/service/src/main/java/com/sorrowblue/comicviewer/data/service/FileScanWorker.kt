package com.sorrowblue.comicviewer.data.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.data.infrastructure.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.model.model.ScanModel
import com.sorrowblue.comicviewer.data.model.util.SortUtil
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random
import kotlinx.coroutines.flow.first

@HiltWorker
internal class FileScanWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val factory: RemoteDataSource.Factory,
    private val fileLocalDataSource: FileModelLocalDataSource,
) : CoroutineWorker(appContext, workerParams) {

    private lateinit var supportExtensions: List<String>

    private val notificationID = Random.nextInt()

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("")
    }

    override suspend fun doWork(): Result {
        val request = FileScanRequest.fromWorkData(inputData) ?: return Result.failure()
        val serverModel = bookshelfLocalDataSource.flow(request.bookshelfModelId).first()!!
        val rootFileModel = fileLocalDataSource.root(serverModel.id)!!
        val fileModel = fileLocalDataSource.findBy(request.bookshelfModelId, request.path)
        val resolveImageFolder = request.resolveImageFolder
        supportExtensions = request.supportExtensions
        //TODO(スキャン種別ごとに変える)
        when (request.scanModel) {
            ScanModel.ALL -> factory.create(serverModel).nestedListFiles(serverModel, rootFileModel, resolveImageFolder, true)
            ScanModel.IN_FOLDER -> factory.create(serverModel).nestedListFiles(serverModel, fileModel!!, resolveImageFolder, false)
            ScanModel.IN_FOLDER_SUB -> factory.create(serverModel).nestedListFiles(serverModel, fileModel!!, resolveImageFolder, false)
        }
        return Result.success()
    }

    private suspend fun RemoteDataSource.nestedListFiles(
        bookshelfModel: BookshelfModel,
        fileModel: FileModel,
        resolveImageFolder: Boolean,
        isNested: Boolean
    ) {
        setProgress(workDataOf("path" to fileModel.path))
        setForeground(createForegroundInfo(fileModel.path))

        val fileModelList = SortUtil.sortedIndex(listFiles(fileModel, resolveImageFolder) {
            SortUtil.filter(it, supportExtensions)
        })
        fileLocalDataSource.updateHistory(fileModel, fileModelList)
        if (isNested) {
            fileModelList.filter { it is FileModel.Folder || it is FileModel.ImageFolder }
                .forEach { nestedListFiles(bookshelfModel, it, resolveImageFolder, true) }
        }
    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val title = "スキャン中"
        val cancel = applicationContext.getString(android.R.string.cancel)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)
        val pendingIntent =
            PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("comicviewer://comicviewer.sorrowblue.com/work?uuid=${id}")
                ),
                PendingIntent.FLAG_IMMUTABLE
            )
        val notification =
            NotificationCompat.Builder(applicationContext, ChannelID.SCAN_BOOKSHELF.id)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(progress)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_twotone_downloading_24)
                .setOngoing(true)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                .addAction(android.R.drawable.ic_delete, cancel, intent)
                .build()

        return ForegroundInfo(notificationID, notification)
    }
}
