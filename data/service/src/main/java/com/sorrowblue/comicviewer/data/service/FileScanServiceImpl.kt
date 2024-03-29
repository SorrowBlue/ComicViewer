package com.sorrowblue.comicviewer.data.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.FileScanService
import com.sorrowblue.comicviewer.domain.model.Scan
import com.sorrowblue.comicviewer.domain.model.file.File
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import logcat.logcat

internal class FileScanServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : FileScanService {
    override suspend fun enqueue(
        file: File,
        scan: Scan,
        resolveImageFolder: Boolean,
        supportExtensions: List<String>,
    ): String {
        val constraints = Constraints.Builder().apply {
            // 有効なネットワーク接続が必要
            setRequiredNetworkType(NetworkType.CONNECTED)
            // ユーザーのデバイスの保存容量が少なすぎる場合以外
            setRequiresStorageNotLow(true)
        }.build()
        logcat { "OneTimeWorkRequest.Builder(FileScanWorker::class.java)" }
        val myWorkRequest = OneTimeWorkRequest.Builder(FileScanWorker::class.java)
            .setConstraints(constraints)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag("observable")
            .setInputData(
                FileScanRequest(
                    file.bookshelfId,
                    file.path,
                    scan,
                    resolveImageFolder,
                    supportExtensions
                ).toWorkData()
            )
            .build()
        val id = myWorkRequest.id
        WorkManager.getInstance(context)
            .enqueue(myWorkRequest)
        return id.toString()
    }
}
