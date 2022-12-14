package com.sorrowblue.comicviewer.data.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ScanTypeModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.util.SortUtil
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FileModelRemoteDataSource
import com.sorrowblue.comicviewer.data.datasource.ServerLocalDataSource
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.notification.createNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.system.measureTimeMillis
import logcat.logcat


private const val NOTIFICATION_ID = 1

@HiltWorker
internal class FileScanWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val serverLocalDataSource: ServerLocalDataSource,
    private val factory: FileModelRemoteDataSource.Factory,
    private val fileLocalDataSource: FileModelLocalDataSource,
) : CoroutineWorker(appContext, workerParams) {

    private val notificationManager = NotificationManagerCompat.from(applicationContext)
    private lateinit var supportExtensions: List<String>

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification(
            applicationContext, ChannelID.SCAN, R.drawable.ic_twotone_downloading_24
        ) {})
    }

    override suspend fun doWork(): Result {
        val request = FileScanRequest.fromWorkData(inputData) ?: return Result.failure()
        setForeground(getForegroundInfo())
        val serverModel = serverLocalDataSource.get(request.serverModelId)!!
        val rootFileModel = fileLocalDataSource.root(serverModel.id)!!
        val fileModel = fileLocalDataSource.findBy(request.serverModelId, request.path)
        val resolveImageFolder = request.resolveImageFolder
        supportExtensions = request.supportExtensions
        when (request.scanTypeModel) {
            ScanTypeModel.FULL -> factory.create(serverModel)
                .nestedListFiles(serverModel, rootFileModel, resolveImageFolder, true)
            ScanTypeModel.QUICK -> factory.create(serverModel)
                .nestedListFiles(serverModel, fileModel!!, resolveImageFolder, false)
        }
        return Result.success()
    }

    private suspend fun FileModelRemoteDataSource.nestedListFiles(
        serverModel: ServerModel,
        fileModel: FileModel,
        resolveImageFolder: Boolean,
        isNested: Boolean
    ) {
        setProgress(workDataOf("path" to fileModel.path))
        notificationManager.notify(NOTIFICATION_ID, createNotification(
            applicationContext, ChannelID.SCAN, R.drawable.ic_twotone_downloading_24
        ) {
            val notificationIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://comicviewer.sorrowblue.com/work?uuid=${id}")
            )
            val pendingIntent =
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            setContentText(fileModel.path)
            setContentIntent(pendingIntent)
            setContentTitle("???????????????")
            setProgress(0, 0, true)
        })

        val fileModelList: List<FileModel>
        if (BuildConfig.DEBUG) {
            measureTimeMillis {
                fileModelList =
                    SortUtil.sortedIndex(listFiles(fileModel, resolveImageFolder) {
                        SortUtil.filter(
                            it,
                            supportExtensions
                        )
                    })
            }.also {
                logcat(tag = "FileScanWorker") { "listFiles(${fileModel.path}). $it ms" }
            }
        } else {
            fileModelList =
                SortUtil.sortedIndex(listFiles(fileModel, resolveImageFolder) {
                    SortUtil.filter(
                        it,
                        supportExtensions
                    )
                })
        }
        fileLocalDataSource.withTransaction {
            // ????????????????????????DB??????????????????????????????
            val deleteFileData = fileLocalDataSource.selectByNotPaths(
                serverModel.id,
                fileModel.path,
                fileModelList.map(FileModel::path)
            )
            // DB????????????
            fileLocalDataSource.deleteAll(deleteFileData)
            // existsFiles DB??????????????????????????????
            // noExistsFiles DB??????????????????????????????
            val (existsFiles, noExistsFiles) = fileModelList.partition {
                fileLocalDataSource.exists(it.serverModelId, it.path)
            }
            // DB????????????????????????
            fileLocalDataSource.registerAll(noExistsFiles)
            // DB????????????????????????
            fileLocalDataSource.updateAll(existsFiles.map(FileModel::simple))
        }
        if (isNested) {
            fileModelList.filter { it is FileModel.Folder || it is FileModel.ImageFolder }
                .forEach { nestedListFiles(serverModel, it, resolveImageFolder, true) }
        }
    }
}
