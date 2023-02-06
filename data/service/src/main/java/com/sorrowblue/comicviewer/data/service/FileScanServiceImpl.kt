package com.sorrowblue.comicviewer.data.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.ScanTypeModel
import com.sorrowblue.comicviewer.data.reporitory.FileScanService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class FileScanServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FileScanService {
    override suspend fun enqueue(
        fileModel: FileModel,
        scanTypeModel: ScanTypeModel,
        resolveImageFolder: Boolean,
        supportExtensions: List<String>
    ): String {
        val constraints = Constraints.Builder().apply {
            // 有効なネットワーク接続が必要
            setRequiredNetworkType(NetworkType.CONNECTED)
            // ユーザーのデバイスの保存容量が少なすぎる場合以外
            setRequiresStorageNotLow(true)
        }.build()
        val myWorkRequest = OneTimeWorkRequest.Builder(FileScanWorker::class.java)
            .setConstraints(constraints)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                FileScanRequest(
                    fileModel.bookshelfModelId,
                    fileModel.path,
                    scanTypeModel,
                    resolveImageFolder,
                    supportExtensions
                ).toWorkData()
            )
            .build()
        val id = myWorkRequest.id
        WorkManager.getInstance(context)
            .enqueueUniqueWork("ExampleServiceImpl", ExistingWorkPolicy.REPLACE, myWorkRequest)
        return id.toString()
    }
}
